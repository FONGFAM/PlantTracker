package com.planttracker.Controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.planttracker.Models.PlantReport;
import com.planttracker.Services.ReportService;
import com.planttracker.Services.PlantReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

     private final ReportService reportService;
     private final PlantReportService plantReportService;

     public ReportController(ReportService reportService, PlantReportService plantReportService) {
          this.reportService = reportService;
          this.plantReportService = plantReportService;
     }

     // Summary report
     @GetMapping("/summary")
     public Map<String, Object> getSummary(Authentication auth) {
          return reportService.generateSummary(auth);
     }

     // Combined report with PlantReport and PlantStatus
     @GetMapping("/combined")
     public Map<String, Object> getCombinedReports(Authentication auth) {
          return reportService.getCombinedReports(auth);
     }

     // Create a report for a plant (plantId in path)
     @PostMapping("/plant/{plantId}")
     public ResponseEntity<PlantReport> addReport(@PathVariable Long plantId,
               @RequestBody PlantReport report,
               Authentication auth) {
          String username = auth.getName();
          PlantReport created = plantReportService.addReport(plantId, username, report);
          return ResponseEntity.status(HttpStatus.CREATED).body(created);
     }

     // Get reports for a plant (ordered by date asc)
     @GetMapping("/plant/{plantId}")
     public ResponseEntity<List<PlantReport>> getReports(@PathVariable Long plantId, Authentication auth) {
          // optional ownership check could be here (or in service)
          List<PlantReport> list = plantReportService.getReportsForPlant(plantId);
          return ResponseEntity.ok(list);
     }

     // Delete a report (only owner of report)
     @DeleteMapping("/plant/{id}")
     public ResponseEntity<Void> deleteReport(@PathVariable Long id, Authentication auth) {
          plantReportService.deleteReport(id, auth.getName());
          return ResponseEntity.noContent().build();
     }

     // Export Excel for a plant
     @GetMapping("/plant/{plantId}/export")
     public void exportPlantReports(@PathVariable Long plantId, HttpServletResponse response, Authentication auth)
               throws IOException {
          // optional: check ownership or admin
          plantReportService.exportReportsToExcel(plantId, response);
     }
}
