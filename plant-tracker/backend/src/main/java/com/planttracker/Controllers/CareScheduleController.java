package com.planttracker.Controllers;

import java.io.IOException;
import com.planttracker.Models.CareSchedule;
import com.planttracker.Services.CareScheduleService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/care-schedules")
public class CareScheduleController {

     private final CareScheduleService service;

     public CareScheduleController(CareScheduleService service) {
          this.service = service;
     }

     /**
      * ✨ SỬA 1: Lấy lịch trình (ĐÃ PHÂN TRANG).
      * - ADMIN: Xem tất cả lịch trình
      * - USER: Chỉ xem lịch trình của cây của mình
      */
     @GetMapping
     public ResponseEntity<Page<CareSchedule>> getAll(
               @RequestParam(defaultValue = "0") int page,
               @RequestParam(defaultValue = "4") int size) {
          Authentication auth = SecurityContextHolder.getContext().getAuthentication();
          String username = auth.getName();
          boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

          Pageable pageable = PageRequest.of(page, size);
          Page<CareSchedule> schedulePage;

          if (isAdmin) {
               // Admin xem tất cả
               schedulePage = service.getAll(pageable);
          } else {
               // User thường chỉ xem của mình
               schedulePage = service.getAllByUsername(username, pageable);
          }

          return ResponseEntity.ok(schedulePage);
     }

     // 🔹 Get by ID (Không cần phân trang)
     @GetMapping("/{id}")
     public ResponseEntity<CareSchedule> getById(@PathVariable Long id) {
          return ResponseEntity.ok(service.getById(id));
     }

     /**
      * ✨ SỬA 2: Lấy lịch trình THEO PLANT ID (ĐÃ PHÂN TRANG).
      * - Nhận tham số 'page' và 'size'.
      * - Tạo đối tượng Pageable.
      * - Gọi hàm service trả về Page.
      * - Trả về ResponseEntity<Page<CareSchedule>>.
      */
     @GetMapping("/plant/{plantId}")
     public ResponseEntity<Page<CareSchedule>> getByPlant(
               @PathVariable Long plantId,
               @RequestParam(defaultValue = "0") int page,
               @RequestParam(defaultValue = "4") int size
     // Optional: Thêm sort @RequestParam(defaultValue = "nextAt,asc") String sort
     ) {
          // Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC,
          // "nextAt"));
          Pageable pageable = PageRequest.of(page, size);
          Page<CareSchedule> schedulePage = service.getActiveByPlantId(plantId, pageable);
          return ResponseEntity.ok(schedulePage);
     }

     /**
      * 🔹 Create schedule rule (Không cần phân trang)
      */
     @PostMapping("/plant/{plantId}")
     public ResponseEntity<CareSchedule> createSchedule(
               @PathVariable Long plantId,
               @RequestBody CareSchedule scheduleData) {

          CareSchedule createdSchedule = service.createSchedule(
                    plantId,
                    scheduleData.getType(),
                    scheduleData.getNextAt(),
                    scheduleData.getFrequency());
          return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
     }

     /**
      * 🔹 Update schedule rule (Không cần phân trang)
      */
     @PutMapping("/{id}")
     public ResponseEntity<CareSchedule> updateSchedule(
               @PathVariable Long id,
               @RequestBody CareSchedule scheduleData) {

          CareSchedule updatedSchedule = service.updateSchedule(
                    id,
                    scheduleData.getType(),
                    scheduleData.getNextAt(),
                    scheduleData.getFrequency());
          return ResponseEntity.ok(updatedSchedule);
     }

     /**
      * 🔹 Deactivate schedule (Không cần phân trang)
      */
     @DeleteMapping("/{id}")
     public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
          service.deactivateSchedule(id);
          return ResponseEntity.noContent().build();
     }

     /**
      * 🔹 Permanently delete schedule (Xóa vĩnh viễn)
      */
     @DeleteMapping("/{id}/permanent")
     public ResponseEntity<Void> permanentlyDeleteSchedule(@PathVariable Long id) {
          service.permanentlyDeleteSchedule(id);
          return ResponseEntity.noContent().build();
     }

     // 🔹 Mark completed (Không cần phân trang)
     @PutMapping("/{id}/complete")
     public ResponseEntity<CareSchedule> markCompleted(@PathVariable Long id) {
          return ResponseEntity.ok(service.markCompleted(id));
     }

     // 🔹 Mark skipped (Không cần phân trang)
     @PutMapping("/{id}/skip")
     public ResponseEntity<CareSchedule> markSkipped(@PathVariable Long id) {
          return ResponseEntity.ok(service.markSkipped(id));
     }

     // 🔹 Generate AI schedules (Không cần phân trang)
     @PostMapping("/plant/{plantId}/ai")
     public ResponseEntity<Void> generateAISchedules(@PathVariable Long plantId) throws IOException {
          service.generateSchedulesAI(plantId);
          return ResponseEntity.ok().build();
     }
}