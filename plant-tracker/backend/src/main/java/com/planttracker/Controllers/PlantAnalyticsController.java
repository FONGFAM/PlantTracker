package com.planttracker.Controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.planttracker.dto.PlantAnalyticsDTO;
import com.planttracker.Services.PlantAnalyticsService;

@RestController
@RequestMapping("/api/analytics")
public class PlantAnalyticsController {

     private final PlantAnalyticsService service;

     public PlantAnalyticsController(PlantAnalyticsService service) {
          this.service = service;
     }

     @GetMapping("/{plantId}")
     public ResponseEntity<PlantAnalyticsDTO> getAnalytics(@PathVariable Long plantId) {
          return ResponseEntity.ok(service.getAnalytics(plantId));
     }
}
