package com.planttracker.Services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

// 👇 Thêm import cho Phân trang
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
     private final AIService aiService; // Đổi tên từ AIService -> aiService

     public CareScheduleService(CareScheduleRepository scheduleRepo,
               PlantRepository plantRepo,
               PlantReportRepository reportRepo,
               PlantStatusRepository statusRepo,
               AIService aiService) { // Sửa tham số constructor
          this.scheduleRepo = scheduleRepo;
          this.plantRepo = plantRepo;
          this.reportRepo = reportRepo;
          this.statusRepo = statusRepo;
          this.aiService = aiService; // Sửa ở đây
     }

     /**
      * Lấy TẤT CẢ lịch trình (đã phân trang).
      */
     public Page<CareSchedule> getAll(Pageable pageable) {
          return scheduleRepo.findAll(pageable);
     }

     /**
      * Lấy TẤT CẢ lịch trình CỦA USER HIỆN TẠI (đã phân trang và chỉ active).
      */
     public Page<CareSchedule> getAllByUsername(String username, Pageable pageable) {
          return scheduleRepo.findByPlant_User_UsernameAndActiveTrue(username, pageable);
     }

     /**
      * Lấy lịch trình theo ID.
      */
     public CareSchedule getById(Long id) {
          return scheduleRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));
     }

     /**
      * Lấy các lịch trình CÒN KÍCH HOẠT theo plantId (đã phân trang).
      */
     public Page<CareSchedule> getActiveByPlantId(Long plantId, Pageable pageable) {
          return scheduleRepo.findByPlant_IdAndActiveTrue(plantId, pageable);
     }

     /**
      * Tạo một quy tắc chăm sóc mới.
      */
     @Transactional
     public CareSchedule createSchedule(Long plantId, String type, LocalDateTime nextAt, String frequency) {
          Plants plant = plantRepo.findById(plantId)
                    .orElseThrow(() -> new RuntimeException("Plant not found"));

          CareSchedule schedule = new CareSchedule();
          schedule.setPlant(plant);
          schedule.setType(type);
          schedule.setNextAt(nextAt);
          schedule.setFrequency(frequency);
          schedule.setActive(true);

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
      * Hủy kích hoạt một quy tắc (xóa mềm).
      */
     @Transactional
     public CareSchedule deactivateSchedule(Long id) {
          CareSchedule schedule = getById(id);
          schedule.setActive(false);
          return scheduleRepo.save(schedule);
     }

     /**
      * Xóa vĩnh viễn một quy tắc khỏi database.
      */
     @Transactional
     public void permanentlyDeleteSchedule(Long id) {
          scheduleRepo.deleteById(id);
     }

     /**
      * Đánh dấu một quy tắc là "đã hoàn thành".
      */
     @Transactional
     public CareSchedule markCompleted(Long id) {
          CareSchedule schedule = getById(id);
          LocalDateTime now = LocalDateTime.now();
          schedule.setLastPerformedAt(now);
          schedule.setNextAt(calculateNextAt(now, schedule.getFrequency())); // Tính lần kế tiếp
          return scheduleRepo.save(schedule);
     }

     /**
      * Đánh dấu một quy tắc là "bỏ qua".
      */
     @Transactional
     public CareSchedule markSkipped(Long id) {
          CareSchedule schedule = getById(id);
          // Chỉ tính lần kế tiếp, không cập nhật lastPerformedAt
          schedule.setNextAt(calculateNextAt(schedule.getNextAt(), schedule.getFrequency()));
          return scheduleRepo.save(schedule);
     }

     /**
      * Tạo lịch bằng AI dựa trên thông tin chi tiết của cây.
      */
     @Transactional
     public void generateSchedulesAI(Long plantId) throws IOException {
          // Lấy thông tin cây, bao gồm cả loại cây (PlantType)
          Plants plant = plantRepo.findById(plantId)
                    .orElseThrow(() -> new RuntimeException("Plant not found"));
          String plantTypeName = Optional.ofNullable(plant.getPlantType())
                    .map(PlantTypes::getTypeName)
                    .orElse("Không rõ loại");

          PlantReport latestReport = reportRepo.findByPlant_IdOrderByDateAsc(plantId)
                    .stream().reduce((first, second) -> second).orElse(null);
          PlantStatus latestStatus = statusRepo.findByPlants_IdOrderByUpdateAtAsc(plantId)
                    .stream().findFirst().orElse(null);
          String statusDescription = Optional.ofNullable(latestStatus)
                    .map(PlantStatus::getDescription)
                    .orElse("Không có mô tả");
          String statusGeneral = Optional.ofNullable(latestStatus)
                    .map(PlantStatus::getStatus)
                    .orElse("Không rõ");

          // === PROMPT ĐƯỢC TỐI ƯU HÓA CAO ĐỘ ===
          String prompt = String.format(
                    """
                              Hãy đóng vai một chuyên gia chăm sóc cây cảnh. Dựa vào thông tin chi tiết dưới đây, hãy gợi ý các quy tắc chăm sóc (lịch trình lặp lại) PHÙ HỢP NHẤT cho cây '%s' bằng TIẾNG VIỆT.

                              THÔNG TIN CHI TIẾT VỀ CÂY:
                              - Loại cây: %s
                              - Trạng thái chung: %s
                              - Mô tả trạng thái gần nhất: %s
                              - Dữ liệu môi trường gần nhất (nếu có): Nhiệt độ: %s°C, Độ ẩm: %s%%, Chiều cao: %s cm.

                              YÊU CẦU QUAN TRỌNG KHI ĐƯA RA LỊCH TRÌNH:
                              1. PHẢI dựa vào LOẠI CÂY và TRẠNG THÁI HIỆN TẠI để đưa ra tần suất chăm sóc phù hợp (VD: Xương rồng cần ít nước hơn Dương xỉ, cây bị vàng lá có thể cần bón phân khác cây khỏe mạnh).
                              2. Chỉ tập trung vào các HÀNH ĐỘNG có thể lên lịch định kỳ.
                              3. KHÔNG gợi ý về điều kiện tĩnh (ánh sáng, đất, chậu).
                              4. Định dạng BẮT BUỘC: 'LOẠI HÀNH ĐỘNG:Tần suất'.
                              5. CẢ LOẠI HÀNH ĐỘNG và TẦN SUẤT đều PHẢI VIẾT BẰNG TIẾNG VIỆT.
                              6. TẦN SUẤT phải thực tế, đa dạng VÀ CỤ THỂ theo tình trạng cây (VD: 'Tưới đẫm khi đất khô hoàn toàn', 'Bón phân loãng 2 tuần/lần nếu cây đang vàng lá', 'Hàng tuần kiểm tra sâu bệnh').
                              7. Các LOẠI HÀNH ĐỘNG hợp lệ (viết hoa, tiếng Việt): TƯỚI NƯỚC, BÓN PHÂN, CẮT TỈA, THAY CHẬU, KIỂM TRA SỨC KHỎE, PHÒNG TRỪ SÂU BỆNH.

                              Chỉ trả về danh sách các quy tắc, KHÔNG giải thích gì thêm.
                              """,
                    plant.getName(),
                    plantTypeName, // Thêm loại cây
                    statusGeneral, // Trạng thái chung
                    statusDescription, // Mô tả chi tiết hơn
                    latestReport != null ? latestReport.getTemperature() : "N/A", // Dùng N/A nếu không có
                    latestReport != null ? latestReport.getHumidity() : "N/A",
                    latestReport != null ? latestReport.getHeight() : "N/A");

          String advice = aiService.getCareAdvice(prompt); // Gọi AI

          // Map loại Tiếng Việt sang Tiếng Anh
          Map<String, String> typeMapping = Map.of(
                    "TƯỚI NƯỚC", "WATER",
                    "BÓN PHÂN", "FERTILIZE",
                    "CẮT TỈA", "PRUNE",
                    "THAY CHẬU", "REPOT",
                    "KIỂM TRA SỨC KHỎE", "HEALTH_CHECK",
                    "PHÒNG TRỪ SÂU BỆNH", "PEST_CONTROL");

          // Logic parse và lưu
          Stream.of(advice.split("\n"))
                    .filter(line -> line.contains(":"))
                    .forEach(line -> {
                         try {
                              String[] parts = line.split(":", 2);
                              String vietnameseType = parts[0].trim().toUpperCase();
                              String frequency = parts[1].trim();
                              String englishType = typeMapping.get(vietnameseType);

                              if (englishType != null) {
                                   CareSchedule schedule = new CareSchedule();
                                   schedule.setPlant(plant);
                                   schedule.setType(englishType);
                                   schedule.setFrequency(frequency); // Lưu tần suất tiếng Việt
                                   schedule.setNextAt(LocalDateTime.now().plusDays(1)); // Bắt đầu từ ngày mai
                                   schedule.setActive(true);
                                   scheduleRepo.save(schedule);
                              } else {
                                   System.err.println("Bỏ qua type không hợp lệ từ AI (Tiếng Việt): " + line);
                              }
                         } catch (Exception e) {
                              System.err.println("Không thể parse dòng AI: " + line + " | Lỗi: " + e.getMessage());
                         }
                    });
     }

     /**
      * Hàm tính ngày tiếp theo dựa trên tần suất.
      * Cần cải thiện để xử lý các tần suất phức tạp hơn.
      */
     private LocalDateTime calculateNextAt(LocalDateTime fromDate, String frequency) {
          if (frequency == null || frequency.isEmpty()) {
               return fromDate.plusDays(1); // Mặc định là ngày tiếp theo nếu không có tần suất
          }
          try {
               // Xử lý "X ngày/lần" hoặc "X-Y ngày/lần"
               if (frequency.contains("ngày/lần")) {
                    String numericPart = frequency.split(" ")[0]; // Lấy phần số (VD: "7", "7-10")
                    if (numericPart.contains("-")) {
                         numericPart = numericPart.split("-")[0]; // Lấy số đầu tiên nếu là khoảng
                    }
                    int days = Integer.parseInt(numericPart.trim());
                    return fromDate.plusDays(days);
               }
               // TODO: Thêm logic cho "tuần/lần", "tháng/lần", "Khi đất khô", "Đầu mùa xuân",
               // v.v.
               // Ví dụ đơn giản cho "Hàng tuần"
               else if (frequency.equalsIgnoreCase("Hàng tuần")) {
                    return fromDate.plusWeeks(1);
               }
               // Ví dụ đơn giản cho "Hàng tháng"
               else if (frequency.equalsIgnoreCase("Hàng tháng")) {
                    return fromDate.plusMonths(1);
               }
               // Cần thêm logic phức tạp hơn cho các trường hợp khác
               else {
                    System.err.println("Tần suất chưa được hỗ trợ: " + frequency + ". Tạm đặt là 7 ngày.");
                    return fromDate.plusDays(7); // Mặc định nếu không hiểu tần suất
               }
          } catch (NumberFormatException e) {
               System.err.println("Lỗi khi parse số ngày từ tần suất: " + frequency);
               return fromDate.plusDays(7); // Mặc định nếu lỗi parse số
          } catch (Exception e) {
               System.err.println("Lỗi không xác định khi tính nextAt cho tần suất: " + frequency);
               return fromDate.plusDays(7); // Mặc định nếu có lỗi khác
          }
     }
}