package com.planttracker.Services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
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
     private final ChatGPTAIService chatGPTAIService;

     public CareScheduleService(CareScheduleRepository scheduleRepo,
               PlantRepository plantRepo,
               PlantReportRepository reportRepo,
               PlantStatusRepository statusRepo,
               ChatGPTAIService chatGPTAIService) {
          this.scheduleRepo = scheduleRepo;
          this.plantRepo = plantRepo;
          this.reportRepo = reportRepo;
          this.statusRepo = statusRepo;
          this.chatGPTAIService = chatGPTAIService;
     }

     public List<CareSchedules> getAll() {
          return scheduleRepo.findAll();
     }

     public CareSchedules getById(Long id) {
          return scheduleRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("Schedule not found"));
     }

     public List<CareSchedules> getByPlantId(Long plantId) {
          return scheduleRepo.findByPlant_Id(plantId);
     }

     @Transactional
     public CareSchedules createSchedule(Long plantId, String activity, LocalDateTime scheduledAt) {
          Plants plant = plantRepo.findById(plantId)
                    .orElseThrow(() -> new RuntimeException("Plant not found"));

          CareSchedules schedule = new CareSchedules();
          schedule.setPlant(plant);
          schedule.setActivity(activity);
          schedule.setScheduledAt(scheduledAt);

          return scheduleRepo.save(schedule);
     }

     @Transactional
     public CareSchedules updateSchedule(Long id, String activity, LocalDateTime scheduledAt) {
          CareSchedules schedule = getById(id);
          schedule.setActivity(activity);
          schedule.setScheduledAt(scheduledAt);
          return scheduleRepo.save(schedule);
     }

     @Transactional
     public void deleteSchedule(Long id) {
          CareSchedules schedule = getById(id);
          scheduleRepo.delete(schedule);
     }

     @Transactional
     public CareSchedules markCompleted(Long id) {
          CareSchedules schedule = getById(id);
          schedule.setCompleted(true);
          return scheduleRepo.save(schedule);
     }

     @Transactional
     public CareSchedules markSkipped(Long id) {
          CareSchedules schedule = getById(id);
          schedule.setCompleted(false); // hoặc logic khác nếu skip cần ghi chú
          return scheduleRepo.save(schedule);
     }

     @Transactional
     public void generateSchedulesAI(Long plantId) throws IOException {
          Plants plant = plantRepo.findById(plantId)
                    .orElseThrow(() -> new RuntimeException("Plant not found"));

          PlantReport latestReport = reportRepo.findByPlant_IdOrderByDateAsc(plantId)
                    .stream().reduce((first, second) -> second).orElse(null);
          PlantStatus latestStatus = statusRepo.findByPlants_IdOrderByUpdateAtAsc(plantId)
                    .stream().findFirst().orElse(null);

          String prompt = String.format(
                    "Cây '%s' hiện trạng '%s'. Nhiệt độ: %s°C, độ ẩm: %s%%, chiều cao: %s cm. "
                              + "Hãy gợi ý lịch chăm sóc tuần tới, liệt kê hành động với thời gian thực tế (tưới nước, bón phân, phòng bệnh).",
                    plant.getName(),
                    latestStatus != null ? latestStatus.getStatus() : "Không rõ",
                    latestReport != null ? latestReport.getTemperature() : "Không rõ",
                    latestReport != null ? latestReport.getHumidity() : "Không rõ",
                    latestReport != null ? latestReport.getHeight() : "Không rõ");

          String advice = chatGPTAIService.getCareAdvice(prompt);

          CareSchedules schedule = new CareSchedules();
          schedule.setPlant(plant);
          schedule.setActivity(advice);
          schedule.setScheduledAt(LocalDateTime.now().plusDays(1));
          scheduleRepo.save(schedule);
     }
}
