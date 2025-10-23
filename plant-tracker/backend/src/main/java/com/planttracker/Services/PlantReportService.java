package com.planttracker.Services;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.planttracker.Models.PlantReport;
import com.planttracker.Models.Plants;
import com.planttracker.Models.Users;
import com.planttracker.Repositories.PlantReportRepository;
import com.planttracker.Repositories.PlantRepository;
import com.planttracker.Repositories.UserRepository;

@Service
public class PlantReportService {

     private final PlantReportRepository reportRepo;
     private final PlantRepository plantRepo;
     private final UserRepository userRepo;

     public PlantReportService(PlantReportRepository reportRepo,
               PlantRepository plantRepo,
               UserRepository userRepo) {
          this.reportRepo = reportRepo;
          this.plantRepo = plantRepo;
          this.userRepo = userRepo;
     }

     @Transactional
     public PlantReport addReport(Long plantId, String username, PlantReport report) {
          Plants plant = plantRepo.findById(plantId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
          Users user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

          // ensure owner or admin: if plant has owner, check match (ownership check can
          // be done in controller/service)
          if (!plant.getUser().getUsername().equals(username)) {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't own this plant");
          }

          // set relations
          report.setPlant(plant);
          report.setUser(user);
          if (report.getDate() == null)
               report.setDate(LocalDate.now());

          return reportRepo.save(report);
     }

     public List<PlantReport> getReportsForPlant(Long plantId) {
          return reportRepo.findByPlant_IdOrderByDateAsc(plantId);
     }

     public void deleteReport(Long id, String username) {
          PlantReport r = reportRepo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
          if (!r.getUser().getUsername().equals(username)) {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
          }
          reportRepo.deleteById(id);
     }

     // Export to excel
     public void exportReportsToExcel(Long plantId, HttpServletResponse response) throws IOException {
          List<PlantReport> reports = getReportsForPlant(plantId);

          Workbook workbook = new XSSFWorkbook();
          Sheet sheet = workbook.createSheet("Plant Reports");

          // header
          Row header = sheet.createRow(0);
          String[] columns = { "Date", "Height (cm)", "Humidity (%)", "Temperature (°C)", "Health", "Note",
                    "Image URL" };
          for (int i = 0; i < columns.length; i++) {
               Cell c = header.createCell(i);
               c.setCellValue(columns[i]);
          }

          int rowIdx = 1;
          for (PlantReport r : reports) {
               Row row = sheet.createRow(rowIdx++);
               row.createCell(0).setCellValue(r.getDate() == null ? "" : r.getDate().toString());
               row.createCell(1).setCellValue(r.getHeight() == null ? "" : String.valueOf(r.getHeight()));
               row.createCell(2).setCellValue(r.getHumidity() == null ? "" : String.valueOf(r.getHumidity()));
               row.createCell(3).setCellValue(r.getTemperature() == null ? "" : String.valueOf(r.getTemperature()));
               row.createCell(4).setCellValue(r.getHealthStatus() == null ? "" : r.getHealthStatus());
               row.createCell(5).setCellValue(r.getNote() == null ? "" : r.getNote());
               row.createCell(6).setCellValue(r.getImageUrl() == null ? "" : r.getImageUrl());
          }

          // autosize
          for (int i = 0; i < columns.length; i++)
               sheet.autoSizeColumn(i);

          response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
          String fileName = "plant_" + plantId + "_reports.xlsx";
          response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
          workbook.write(response.getOutputStream());
          workbook.close();
     }
}
