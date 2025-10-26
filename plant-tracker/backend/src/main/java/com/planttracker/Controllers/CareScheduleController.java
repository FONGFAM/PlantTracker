package com.planttracker.Controllers;

import java.io.IOException;
// Import Model mới (số ít)
import com.planttracker.Models.CareSchedule;
import com.planttracker.Services.CareScheduleService;
import java.util.List;

import org.springframework.http.HttpStatus; // Cần import
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/care-schedules")
public class CareScheduleController {

     private final CareScheduleService service;

     public CareScheduleController(CareScheduleService service) {
          this.service = service;
     }

     // 🔹 All schedules
     @GetMapping
     public ResponseEntity<List<CareSchedule>> getAll() {
          return ResponseEntity.ok(service.getAll());
     }

     // 🔹 Get by ID
     @GetMapping("/{id}")
     public ResponseEntity<CareSchedule> getById(@PathVariable Long id) {
          return ResponseEntity.ok(service.getById(id));
     }

     // 🔹 Get ACTIVE schedules for a plant
     @GetMapping("/plant/{plantId}")
     public ResponseEntity<List<CareSchedule>> getByPlant(@PathVariable Long plantId) {
          // Gọi hàm mới: chỉ lấy các lịch còn active
          return ResponseEntity.ok(service.getActiveByPlantId(plantId));
     }

     /**
      * 🔹 Create schedule rule
      * Thay đổi: Dùng @RequestBody để nhận JSON
      */
     @PostMapping("/plant/{plantId}")
     public ResponseEntity<CareSchedule> createSchedule(
               @PathVariable Long plantId,
               @RequestBody CareSchedule scheduleData) { // Nhận JSON body

          CareSchedule createdSchedule = service.createSchedule(
                    plantId,
                    scheduleData.getType(),
                    scheduleData.getNextAt(),
                    scheduleData.getFrequency());
          // Trả về 201 Created (chuẩn REST)
          return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
     }

     /**
      * 🔹 Update schedule rule
      * Thay đổi: Dùng @RequestBody để nhận JSON
      */
     @PutMapping("/{id}")
     public ResponseEntity<CareSchedule> updateSchedule(
               @PathVariable Long id,
               @RequestBody CareSchedule scheduleData) { // Nhận JSON body

          CareSchedule updatedSchedule = service.updateSchedule(
                    id,
                    scheduleData.getType(),
                    scheduleData.getNextAt(),
                    scheduleData.getFrequency());
          return ResponseEntity.ok(updatedSchedule);
     }

     /**
      * 🔹 Deactivate schedule (Soft Delete)
      * Thay đổi: Gọi hàm deactivateSchedule và trả về 204 No Content
      */
     @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
          service.deactivateSchedule(id); // Gọi hàm "xóa mềm" mới
          return ResponseEntity.noContent().build(); // Trả về 204 No Content (chuẩn REST)
     }

     // 🔹 Mark completed (Logic đã được cập nhật trong service)
     @PutMapping("/{id}/complete")
     public ResponseEntity<CareSchedule> markCompleted(@PathVariable Long id) {
          return ResponseEntity.ok(service.markCompleted(id));
     }

     // 🔹 Mark skipped (Logic đã được cập nhật trong service)
     @PutMapping("/{id}/skip")
     public ResponseEntity<CareSchedule> markSkipped(@PathVariable Long id) {
          return ResponseEntity.ok(service.markSkipped(id));
     }

     // 🔹 Generate AI schedules (Endpoint giữ nguyên, logic trong service đã được
     // cập nhật)
     @PostMapping("/plant/{plantId}/ai")
     public ResponseEntity<Void> generateAISchedules(@PathVariable Long plantId) throws IOException {
          service.generateSchedulesAI(plantId);
          return ResponseEntity.ok().build();
     }
}