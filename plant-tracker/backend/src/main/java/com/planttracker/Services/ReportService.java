package com.planttracker.Services;

import com.planttracker.Repositories.PlantRepository;
import com.planttracker.Repositories.PlantReportRepository;
import com.planttracker.Repositories.PlantStatusRepository;
import com.planttracker.Models.Plants;
import com.planttracker.Models.PlantReport;
import com.planttracker.Models.PlantStatus;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

     private final PlantRepository plantRepository;
     private final PlantReportRepository plantReportRepository;
     private final PlantStatusRepository plantStatusRepository;

     public ReportService(PlantRepository plantRepository,
               PlantReportRepository plantReportRepository,
               PlantStatusRepository plantStatusRepository) {
          this.plantRepository = plantRepository;
          this.plantReportRepository = plantReportRepository;
          this.plantStatusRepository = plantStatusRepository;
     }

     // 🔹 Lấy Authentication hiện tại từ SecurityContext
     private Authentication getAuth() {
          return SecurityContextHolder.getContext().getAuthentication();
     }

     // 🔹 Kiểm tra quyền admin
     private boolean isAdmin() {
          Authentication auth = getAuth();
          if (auth == null || auth.getAuthorities() == null)
               return false;

          return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(r -> r.equals("ROLE_ADMIN"));
     }

     public Map<String, Object> generateSummary(Authentication auth) {
          Authentication currentAuth = getAuth();
          if (currentAuth == null)
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

          List<Plants> plants;
          if (isAdmin()) {
               plants = plantRepository.findAll();
          } else {
               plants = plantRepository.findByUser_Username(currentAuth.getName());
          }

          long totalPlants = plants.size();

          // Đếm số cây theo loại
          Map<String, Long> plantsByType = plants.stream()
                    .collect(Collectors.groupingBy(
                              p -> p.getPlantType().getTypeName(),
                              Collectors.counting()));

          // Cây mới trong tháng này
          LocalDate now = LocalDate.now();
          long newPlantsThisMonth = plants.stream()
                    .filter(p -> p.getCreateDate().toLocalDate().getMonthValue() == now.getMonthValue()
                              && p.getCreateDate().toLocalDate().getYear() == now.getYear())
                    .count();

          Map<String, Object> result = new HashMap<>();
          result.put("totalPlants", totalPlants);
          result.put("plantsByType", plantsByType);
          result.put("newPlantsThisMonth", newPlantsThisMonth);

          return result;
     }

     public Map<String, Object> getCombinedReports(Authentication auth) {
          Authentication currentAuth = getAuth();
          if (currentAuth == null)
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

          List<Plants> plants;
          if (isAdmin()) {
               plants = plantRepository.findAll();
          } else {
               plants = plantRepository.findByUser_Username(currentAuth.getName());
          }

          List<Map<String, Object>> combinedReports = new ArrayList<>();

          for (Plants plant : plants) {
               Map<String, Object> plantReport = new HashMap<>();
               plantReport.put("plantId", plant.getId());
               plantReport.put("plantName", plant.getName());
               plantReport.put("plantType", plant.getPlantType().getTypeName());
               plantReport.put("createDate", plant.getCreateDate());

               // Lấy PlantReports cho cây này
               List<PlantReport> reports = plantReportRepository.findByPlant_IdOrderByDateAsc(plant.getId());
               plantReport.put("plantReports", reports);

               // Lấy PlantStatuses cho cây này
               List<PlantStatus> statuses = plantStatusRepository.findByPlants_IdOrderByUpdateAtAsc(plant.getId());
               plantReport.put("plantStatuses", statuses);

               combinedReports.add(plantReport);
          }

          Map<String, Object> result = new HashMap<>();
          result.put("totalPlants", plants.size());
          result.put("combinedReports", combinedReports);

          return result;
     }
}
