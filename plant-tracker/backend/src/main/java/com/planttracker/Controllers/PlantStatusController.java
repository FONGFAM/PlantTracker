package com.planttracker.Controllers;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.planttracker.Models.PlantStatus;
import com.planttracker.Services.PlantStatusService;

@RestController
@RequestMapping("/api/plant-status")
public class PlantStatusController {

     private final PlantStatusService service;

     public PlantStatusController(PlantStatusService service) {
          this.service = service;
     }

     // 🔹 Lấy tất cả trạng thái của một cây
     @GetMapping("/plant/{plantId}")
     public ResponseEntity<List<PlantStatus>> getPlantStatuses(@PathVariable Long plantId) {
          return ResponseEntity.ok(service.getPlantStatuses(plantId));
     }

     // 🔹 Lấy trạng thái mới nhất của cây
     @GetMapping("/plant/{plantId}/latest")
     public ResponseEntity<PlantStatus> getLatestStatus(@PathVariable Long plantId) {
          return ResponseEntity.ok(service.getLatestStatus(plantId));
     }

     // 🔹 Tạo trạng thái mới cho cây
     @PostMapping("/plant/{plantId}")
     public ResponseEntity<PlantStatus> createStatus(
               @PathVariable Long plantId,
               @RequestBody PlantStatus status) {
          return ResponseEntity.ok(service.createStatus(plantId, status));
     }

     // 🔹 Cập nhật trạng thái
     @PutMapping("/{statusId}")
     public ResponseEntity<PlantStatus> updateStatus(
               @PathVariable Long statusId,
               @RequestBody PlantStatus status) {
          return ResponseEntity.ok(service.updateStatus(statusId, status));
     }

     // 🔹 Xóa trạng thái
     @DeleteMapping("/{statusId}")
     public ResponseEntity<Void> deleteStatus(@PathVariable Long statusId) {
          service.deleteStatus(statusId);
          return ResponseEntity.noContent().build();
     }
}