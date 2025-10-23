package com.planttracker.Services;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planttracker.dto.PlantAnalyticsDTO;
import com.planttracker.Models.PlantReport;
import com.planttracker.Models.PlantStatus;
import com.planttracker.Models.Plants;
import com.planttracker.Repositories.PlantReportRepository;
import com.planttracker.Repositories.PlantRepository;
import com.planttracker.Repositories.PlantStatusRepository;

@Service
@Transactional
public class PlantAnalyticsExcelService {

     private final PlantReportRepository reportRepo;
     private final PlantStatusRepository statusRepo;
     private final PlantRepository plantRepo;
     private final PlantAnalyticsService analyticsService;

     public PlantAnalyticsExcelService(PlantReportRepository reportRepo,
               PlantStatusRepository statusRepo,
               PlantRepository plantRepo,
               PlantAnalyticsService analyticsService) {
          this.reportRepo = reportRepo;
          this.statusRepo = statusRepo;
          this.plantRepo = plantRepo;
          this.analyticsService = analyticsService;
     }

     public void exportAnalyticsToExcel(Long plantId, HttpServletResponse response) throws IOException {
          Plants plant = plantRepo.findById(plantId).orElseThrow(() -> new RuntimeException("Plant not found"));
          List<PlantReport> reports = reportRepo.findByPlant_IdOrderByDateAsc(plantId);
          List<PlantStatus> statuses = statusRepo.findByPlants_IdOrderByUpdateAtAsc(plantId);

          PlantAnalyticsDTO analytics = analyticsService.getAnalytics(plantId);

          Workbook workbook = new XSSFWorkbook();

          // ---- Sheet 1: Chi tiết reports ----
          Sheet detailSheet = workbook.createSheet("Reports Detail");
          Row header = detailSheet.createRow(0);
          String[] columns = { "Date", "Height", "Humidity", "Temperature", "Health Status", "Note", "Image URL" };
          for (int i = 0; i < columns.length; i++) {
               Cell c = header.createCell(i);
               c.setCellValue(columns[i]);
          }

          int rowIdx = 1;
          DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
          for (PlantReport r : reports) {
               Row row = detailSheet.createRow(rowIdx++);
               row.createCell(0).setCellValue(r.getDate() != null ? r.getDate().toString() : "");
               row.createCell(1).setCellValue(r.getHeight() != null ? r.getHeight() : 0);
               row.createCell(2).setCellValue(r.getHumidity() != null ? r.getHumidity() : 0);
               row.createCell(3).setCellValue(r.getTemperature() != null ? r.getTemperature() : 0);
               row.createCell(4).setCellValue(r.getHealthStatus() != null ? r.getHealthStatus() : "");
               row.createCell(5).setCellValue(r.getNote() != null ? r.getNote() : "");
               row.createCell(6).setCellValue(r.getImageUrl() != null ? r.getImageUrl() : "");
          }
          for (int i = 0; i < columns.length; i++)
               detailSheet.autoSizeColumn(i);

          // ---- Sheet 2: Tổng hợp analytics ----
          Sheet summarySheet = workbook.createSheet("Analytics Summary");
          String[][] summaryData = {
                    { "Plant Name", plant.getName() },
                    { "Total Reports", analytics.getTotalReports().toString() },
                    { "Average Height", analytics.getAvgHeight().toString() },
                    { "Average Humidity", analytics.getAvgHumidity().toString() },
                    { "Average Temperature", analytics.getAvgTemperature().toString() },
                    { "Total Watered", analytics.getTotalWatered().toString() },
                    { "Total Fertilized", analytics.getTotalFertilized().toString() },
                    { "Total Sick Days", analytics.getTotalSickDays().toString() }
          };

          for (int i = 0; i < summaryData.length; i++) {
               Row row = summarySheet.createRow(i);
               row.createCell(0).setCellValue(summaryData[i][0]);
               row.createCell(1).setCellValue(summaryData[i][1]);
          }
          summarySheet.autoSizeColumn(0);
          summarySheet.autoSizeColumn(1);

          // ---- Xuất file ----
          response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
          String fileName = "plant_" + plantId + "_analytics.xlsx";
          response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

          workbook.write(response.getOutputStream());
          workbook.close();
     }
}
