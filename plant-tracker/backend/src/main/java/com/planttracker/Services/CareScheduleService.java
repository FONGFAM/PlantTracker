package com.planttracker.Services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream; // Cần import này
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.planttracker.Models.*;
import com.planttracker.Repositories.*;

@Service
public class CareScheduleService {

     private final CareScheduleRepository scheduleRepo;
     private final PlantRepository plantRepo;
     private final PlantReportRepository reportRepo;
     private final PlantStatusRepository statusRepo;
     private final AIService AIService;

     public CareScheduleService(CareScheduleRepository scheduleRepo,
               PlantRepository plantRepo,
               PlantReportRepository reportRepo,
               PlantStatusRepository statusRepo,
               AIService AIService) {
          this.scheduleRepo = scheduleRepo;
          this.plantRepo = plantRepo;
          this.reportRepo = reportRepo;
          this.statusRepo = statusRepo;
          this.AIService = AIService;
     }

     public List<CareSchedule> getAll() {
          return scheduleRepo.findAll();
     }

     public CareSchedule getById(Long id) {
          return scheduleRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));
     }

     // Lấy các lịch trình CÒN KÍCH HOẠT (active) theo plantId
     public List<CareSchedule> getActiveByPlantId(Long plantId) {
          return scheduleRepo.findByPlant_IdAndActiveTrue(plantId);
     }

     /**
      * Tạo một quy tắc (rule) chăm sóc mới.
      */
     @Transactional
     public CareSchedule createSchedule(Long plantId, String type, LocalDateTime nextAt, String frequency) {
          Plants plant = plantRepo.findById(plantId)
                    .orElseThrow(() -> new RuntimeException("Plant not found"));

          CareSchedule schedule = new CareSchedule();
          schedule.setPlant(plant);
          schedule.setType(type); // Sửa 'activity' -> 'type' (VD: "WATER", "FERTILIZE")
          schedule.setNextAt(nextAt); // Sửa 'scheduledAt' -> 'nextAt'
          schedule.setFrequency(frequency); // Thêm 'frequency' (VD: "7 ngày/lần")
          schedule.setActive(true); // Đảm bảo nó được kích hoạt

          return scheduleRepo.save(schedule);
     }

     /**
      * Cập nhật một quy tắc chăm sóc.
      */
     @Transactional
     public CareSchedule updateSchedule(Long id, String type, LocalDateTime nextAt, String frequency) {
          CareSchedule schedule = getById(id);
          schedule.setType(type);
          schedule.setNextAt(nextAt);
          schedule.setFrequency(frequency);
          return scheduleRepo.save(schedule);
     }

     /**
      * Xóa mềm (soft-delete) một quy tắc.
      * Thay vì xóa, ta đặt nó thành 'không kích hoạt'.
      */
     @Transactional
     public CareSchedule deactivateSchedule(Long id) {
          CareSchedule schedule = getById(id);
          schedule.setActive(false);
          return scheduleRepo.save(schedule);
     }

     /**
      * Đánh dấu một quy tắc là "đã hoàn thành".
      * Logic chính: Cập nhật lastPerformedAt và tính toán nextAt mới.
      */
     @Transactional
     public CareSchedule markCompleted(Long id) {
          CareSchedule schedule = getById(id);

          LocalDateTime now = LocalDateTime.now();
          schedule.setLastPerformedAt(now);

          // Tính toán lần tiếp theo dựa trên 'frequency'
          schedule.setNextAt(calculateNextAt(now, schedule.getFrequency()));

          return scheduleRepo.save(schedule);
     }

     /**
      * Đánh dấu một quy tắc là "bỏ qua".
      * Logic: Không cập nhật 'lastPerformedAt', chỉ tính 'nextAt' mới.
      */
     @Transactional
     public CareSchedule markSkipped(Long id) {
          CareSchedule schedule = getById(id);

          // Tính toán lần tiếp theo dựa trên 'nextAt' HIỆN TẠI, không phải 'now'
          schedule.setNextAt(calculateNextAt(schedule.getNextAt(), schedule.getFrequency()));

          return scheduleRepo.save(schedule);
     }

     /**
      * AI tạo ra các *quy tắc* chăm sóc (thay vì 1 hành động).
      */
     @Transactional
     public void generateSchedulesAI(Long plantId) throws IOException {
          Plants plant = plantRepo.findById(plantId)
                    .orElseThrow(() -> new RuntimeException("Plant not found"));

          PlantReport latestReport = reportRepo.findByPlant_IdOrderByDateAsc(plantId)
                    .stream().reduce((first, second) -> second).orElse(null);
          PlantStatus latestStatus = statusRepo.findByPlants_IdOrderByUpdateAtAsc(plantId)
                    .stream().findFirst().orElse(null);

          // Sửa lại prompt: Yêu cầu AI trả về QUY TẮC, không phải hành động
          String prompt = String.format(
                    "Cây '%s' hiện trạng '%s'. Nhiệt độ: %s°C, độ ẩm: %s%%, chiều cao: %s cm. "
                              + "Hãy gợi ý các quy tắc chăm sóc (VD: 'WATER:7 ngày/lần', 'FERTILIZE:30 ngày/lần', 'PRUNE:90 ngày/lần'). "
                              + "Chỉ trả về danh sách, mỗi quy tắc trên một dòng. KHÔNG giải thích gì thêm.",
                    plant.getName(),
                    latestStatus != null ? latestStatus.getStatus() : "Không rõ",
                    latestReport != null ? latestReport.getTemperature() : "Không rõ",
                    latestReport != null ? latestReport.getHumidity() : "Không rõ",
                    latestReport != null ? latestReport.getHeight() : "Không rõ");

          String advice = AIService.getCareAdvice(prompt);

          // Phân tích (parse) phản hồi của AI và tạo nhiều lịch
          Stream.of(advice.split("\n")) // Tách các dòng
                    .filter(line -> line.contains(":")) // Lọc các dòng có định dạng
                    .forEach(line -> {
                         try {
                              String[] parts = line.split(":");
                              String type = parts[0].trim().toUpperCase(); // VD: "WATER"
                              String frequency = parts[1].trim(); // VD: "7 ngày/lần"

                              CareSchedule schedule = new CareSchedule();
                              schedule.setPlant(plant);
                              schedule.setType(type);
                              schedule.setFrequency(frequency);
                              // Đặt lịch đầu tiên là ngày mai
                              schedule.setNextAt(LocalDateTime.now().plusDays(1));
                              schedule.setActive(true);

                              scheduleRepo.save(schedule);
                         } catch (Exception e) {
                              // Bỏ qua nếu dòng không đúng định dạng
                              System.err.println("Không thể parse dòng AI: " + line);
                         }
                    });
     }

     // --- HÀM HỖ TRỢ (HELPER) ---

     /**
      * Hàm đơn giản để tính ngày tiếp theo.
      * Cần logic phức tạp hơn cho "tuần", "tháng".
      */
     private LocalDateTime calculateNextAt(LocalDateTime fromDate, String frequency) {
          if (frequency == null || frequency.isEmpty()) {
               return fromDate.plusDays(1); // Mặc định
          }

          try {
               // Logic đơn giản: chỉ xử lý "X ngày/lần"
               if (frequency.contains("ngày/lần")) {
                    String[] parts = frequency.split(" ");
                    int days = Integer.parseInt(parts[0]);
                    return fromDate.plusDays(days);
               }
               // TODO: Thêm logic cho "tuần/lần", "tháng/lần"

               else {
                    return fromDate.plusDays(7); // Mặc định nếu không hiểu
               }
          } catch (Exception e) {
               System.err.println("Không thể parse frequency: " + frequency);
               return fromDate.plusDays(7); // Mặc định nếu lỗi
          }
     }
}