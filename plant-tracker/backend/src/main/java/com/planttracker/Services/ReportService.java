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

     // 🔹 Normalize health status to Vietnamese
     private String normalizeHealthStatus(String status) {
          if (status == null)
               return "Không xác định";

          String s = status.toLowerCase();
          if (s.contains("excellent") || s.contains("xuất sắc"))
               return "Xuất sắc";
          if (s.contains("good") || s.contains("khỏe") || s.contains("tốt"))
               return "Khỏe mạnh";
          if (s.contains("fair") || s.contains("trung bình"))
               return "Trung bình";
          if (s.contains("warning") || s.contains("cảnh báo"))
               return "Cảnh báo";
          if (s.contains("sick") || s.contains("bệnh") || s.contains("yếu"))
               return "Bệnh";
          if (s.contains("critical") || s.contains("nghiêm trọng"))
               return "Nghiêm trọng";

          return "Không xác định";
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

          // Calculate health statistics from PlantReports
          List<PlantReport> allReports = new ArrayList<>();
          for (Plants plant : plants) {
               allReports.addAll(plantReportRepository.findByPlant_IdOrderByDateAsc(plant.getId()));
          }

          // Count health status from reports
          Map<String, Long> healthStats = new HashMap<>();
          long totalReportsWithHealth = 0;
          long healthyCount = 0;

          for (PlantReport report : allReports) {
               if (report.getHealthStatus() != null && !report.getHealthStatus().isEmpty()) {
                    totalReportsWithHealth++;
                    String status = report.getHealthStatus().toLowerCase();

                    // Count for health rate calculation
                    if (status.contains("good") || status.contains("excellent") ||
                              status.contains("khỏe") || status.contains("tốt")) {
                         healthyCount++;
                    }

                    // Group by status for chart
                    String normalizedStatus = normalizeHealthStatus(status);
                    healthStats.put(normalizedStatus, healthStats.getOrDefault(normalizedStatus, 0L) + 1);
               }
          }

          // Calculate health rate percentage
          String healthRate = "N/A";
          if (totalReportsWithHealth > 0) {
               double rate = (healthyCount * 100.0) / totalReportsWithHealth;
               healthRate = String.format("%.1f%%", rate);
          }

          // Count recent reports (last 7 days)
          LocalDate sevenDaysAgo = now.minusDays(7);
          long recentReports = allReports.stream()
                    .filter(r -> r.getDate().isAfter(sevenDaysAgo))
                    .count();

          // Calculate monthly report trends (last 6 months)
          Map<String, Long> monthlyReports = new LinkedHashMap<>();
          for (int i = 5; i >= 0; i--) {
               LocalDate monthDate = now.minusMonths(i);
               String monthKey = monthDate.getMonth().toString().substring(0, 3) + " " + monthDate.getYear();

               long count = allReports.stream()
                         .filter(r -> r.getDate().getMonthValue() == monthDate.getMonthValue()
                                   && r.getDate().getYear() == monthDate.getYear())
                         .count();

               monthlyReports.put(monthKey, count);
          }

          Map<String, Object> result = new HashMap<>();
          result.put("totalPlants", totalPlants);
          result.put("plantsByType", plantsByType);
          result.put("newPlantsThisMonth", newPlantsThisMonth);

          // Add missing fields for frontend
          result.put("totalTypes", plantsByType.size());
          result.put("recentReports", recentReports);
          result.put("healthRate", healthRate);
          result.put("healthStats", healthStats); // Add health statistics for chart
          result.put("monthlyReports", monthlyReports); // Add monthly trend data

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
