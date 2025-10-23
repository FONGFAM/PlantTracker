package com.planttracker.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.planttracker.dto.PlantAnalyticsDTO;
import com.planttracker.Models.PlantReport;
import com.planttracker.Models.PlantStatus;
import com.planttracker.Models.Plants;
import com.planttracker.Repositories.PlantReportRepository;
import com.planttracker.Repositories.PlantRepository;
import com.planttracker.Repositories.PlantStatusRepository;

@Service
@Transactional
public class PlantAnalyticsService {

     private final PlantReportRepository reportRepo;
     private final PlantStatusRepository statusRepo;
     private final PlantRepository plantRepo;

     public PlantAnalyticsService(PlantReportRepository reportRepo,
               PlantStatusRepository statusRepo,
               PlantRepository plantRepo) {
          this.reportRepo = reportRepo;
          this.statusRepo = statusRepo;
          this.plantRepo = plantRepo;
     }

     public PlantAnalyticsDTO getAnalytics(Long plantId) {
          Plants plant = plantRepo.findById(plantId)
                    .orElseThrow(() -> new RuntimeException("Plant not found"));

          List<PlantReport> reports = reportRepo.findByPlant_IdOrderByDateAsc(plantId);
          List<PlantStatus> statuses = statusRepo.findByPlants_IdOrderByUpdateAtAsc(plantId);

          PlantAnalyticsDTO dto = new PlantAnalyticsDTO(plant.getId(), plant.getName());
          dto.setTotalReports((long) reports.size());

          dto.setAvgHeight(reports.stream()
                    .filter(r -> r.getHeight() != null)
                    .mapToDouble(r -> r.getHeight())
                    .average().orElse(0));

          dto.setAvgHumidity(reports.stream()
                    .filter(r -> r.getHumidity() != null)
                    .mapToDouble(r -> r.getHumidity())
                    .average().orElse(0));

          dto.setAvgTemperature(reports.stream()
                    .filter(r -> r.getTemperature() != null)
                    .mapToDouble(r -> r.getTemperature())
                    .average().orElse(0));

          dto.setTotalWatered(statuses.stream()
                    .filter(s -> "WATERED".equalsIgnoreCase(s.getStatus()))
                    .count());

          dto.setTotalFertilized(statuses.stream()
                    .filter(s -> "FERTILIZED".equalsIgnoreCase(s.getStatus()))
                    .count());

          dto.setTotalSickDays(statuses.stream()
                    .filter(s -> "SICK".equalsIgnoreCase(s.getStatus()))
                    .count());

          return dto;
     }
}
