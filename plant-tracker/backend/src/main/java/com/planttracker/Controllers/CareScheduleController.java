package com.planttracker.Controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.planttracker.Models.CareSchedules;
import com.planttracker.Services.CareScheduleService;

@RestController
@RequestMapping("/api/care-schedules")
public class CareScheduleController {

     private final CareScheduleService service;

     public CareScheduleController(CareScheduleService service) {
          this.service = service;
     }

     // 🔹 All schedules
     @GetMapping
     public ResponseEntity<List<CareSchedules>> getAll() {
          return ResponseEntity.ok(service.getAll());
     }

     // 🔹 Get by ID
     @GetMapping("/{id}")
     public ResponseEntity<CareSchedules> getById(@PathVariable Long id) {
          return ResponseEntity.ok(service.getById(id));
     }

     // 🔹 Get schedules for a plant
     @GetMapping("/plant/{plantId}")
     public ResponseEntity<List<CareSchedules>> getByPlant(@PathVariable Long plantId) {
          return ResponseEntity.ok(service.getByPlantId(plantId));
     }

     // 🔹 Create schedule
     @PostMapping("/plant/{plantId}")
     public ResponseEntity<CareSchedules> createSchedule(
               @PathVariable Long plantId,
               @RequestParam String activity,
               @RequestParam LocalDateTime scheduledAt) {
          return ResponseEntity.ok(service.createSchedule(plantId, activity, scheduledAt));
     }

     // 🔹 Update schedule
     @PutMapping("/{id}")
     public ResponseEntity<CareSchedules> updateSchedule(
               @PathVariable Long id,
               @RequestParam String activity,
               @RequestParam LocalDateTime scheduledAt) {
          return ResponseEntity.ok(service.updateSchedule(id, activity, scheduledAt));
     }

     // 🔹 Delete schedule
     @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
          service.deleteSchedule(id);
          return ResponseEntity.ok().build();
     }

     // 🔹 Mark completed
     @PutMapping("/{id}/complete")
     public ResponseEntity<CareSchedules> markCompleted(@PathVariable Long id) {
          return ResponseEntity.ok(service.markCompleted(id));
     }

     // 🔹 Mark skipped
     @PutMapping("/{id}/skip")
     public ResponseEntity<CareSchedules> markSkipped(@PathVariable Long id) {
          return ResponseEntity.ok(service.markSkipped(id));
     }

     // 🔹 Generate AI schedules
     @PostMapping("/plant/{plantId}/ai")
     public ResponseEntity<Void> generateAISchedules(@PathVariable Long plantId) throws IOException {
          service.generateSchedulesAI(plantId);
          return ResponseEntity.ok().build();
     }
}
