package com.planttracker.Controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

     // Get reports with pagination (ordered by date desc)
     @GetMapping("/plant/{plantId}/paginated")
     public ResponseEntity<Page<PlantReport>> getReportsPaginated(
               @PathVariable Long plantId,
               @RequestParam(defaultValue = "0") int page,
               @RequestParam(defaultValue = "10") int size,
               Authentication auth) {
          Pageable pageable = PageRequest.of(page, size);
          Page<PlantReport> reports = plantReportService.getReportsForPlantPaginated(plantId, pageable);
          return ResponseEntity.ok(reports);
     }

     // Delete a report (only owner of report)
     @DeleteMapping("/plant/{id}")
     public ResponseEntity<Void> deleteReport(@PathVariable Long id, Authentication auth) {
          plantReportService.deleteReport(id, auth.getName());
          return ResponseEntity.noContent().build();
     }

     // Bulk delete reports
     @DeleteMapping("/bulk")
     public ResponseEntity<String> bulkDeleteReports(@RequestBody List<Long> ids, Authentication auth) {
          plantReportService.bulkDeleteReports(ids, auth.getName());
          return ResponseEntity.ok("Đã xóa " + ids.size() + " báo cáo");
     }

     // Bulk create reports for a plant
     @PostMapping("/plant/{plantId}/bulk")
     public ResponseEntity<List<PlantReport>> bulkAddReports(
               @PathVariable Long plantId,
               @RequestBody List<PlantReport> reports,
               Authentication auth) {
          String username = auth.getName();
          List<PlantReport> created = plantReportService.bulkAddReports(plantId, username, reports);
          return ResponseEntity.status(HttpStatus.CREATED).body(created);
     }

     // Export Excel for a plant
     @GetMapping("/plant/{plantId}/export")
     public void exportPlantReports(@PathVariable Long plantId, HttpServletResponse response, Authentication auth)
               throws IOException {
          // optional: check ownership or admin
          plantReportService.exportReportsToExcel(plantId, response);
     }
}
