package com.planttracker.Controllers;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.*;

import com.planttracker.Services.PlantAnalyticsExcelService;

@RestController
@RequestMapping("/api/analytics")
public class PlantAnalyticsExcelController {

     private final PlantAnalyticsExcelService excelService;

     public PlantAnalyticsExcelController(PlantAnalyticsExcelService excelService) {
          this.excelService = excelService;
     }

     @GetMapping("/export/{plantId}")
     public void exportExcel(@PathVariable Long plantId, HttpServletResponse response) throws IOException {
          excelService.exportAnalyticsToExcel(plantId, response);
     }
}
