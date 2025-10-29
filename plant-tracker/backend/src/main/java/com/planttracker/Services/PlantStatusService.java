package com.planttracker.Services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.planttracker.Models.Plants;
import com.planttracker.Models.PlantStatus;
import com.planttracker.Repositories.PlantRepository;
import com.planttracker.Repositories.PlantStatusRepository;
import org.springframework.security.core.GrantedAuthority;

@Service
public class PlantStatusService {

     private final PlantStatusRepository statusRepo;
     private final PlantRepository plantRepo;

     public PlantStatusService(PlantStatusRepository statusRepo, PlantRepository plantRepo) {
          this.statusRepo = statusRepo;
          this.plantRepo = plantRepo;
     }

     // 🔹 Lấy Authentication hiện tại
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
          if (s.contains("good") || s.contains("khỏe") || s.contains("tốt") || s.contains("healthy"))
               return "Khỏe mạnh";
          if (s.contains("fair") || s.contains("trung bình"))
               return "Trung bình";
          if (s.contains("warning") || s.contains("cảnh báo"))
               return "Cảnh báo";
          if (s.contains("sick") || s.contains("bệnh") || s.contains("yếu"))
               return "Bệnh";
          if (s.contains("critical") || s.contains("nghiêm trọng"))
               return "Nghiêm trọng";

          return status; // Return original if no match
     }

     // 🔹 Lấy tất cả trạng thái của một cây
     public List<PlantStatus> getPlantStatuses(Long plantId) {
          Authentication auth = getAuth();
          if (auth == null) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
          }

          // Admin có thể xem tất cả, user chỉ xem của mình
          if (!isAdmin()) {
               // Kiểm tra quyền truy cập cây
               plantRepo.findByIdAndUser_Username(plantId, auth.getName())
                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                                   "Access denied to this plant"));
          } else {
               // Admin: verify plant exists
               plantRepo.findById(plantId)
                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
          }

          List<PlantStatus> statuses = statusRepo.findByPlants_IdOrderByUpdateAtAsc(plantId);

          // Normalize all statuses to Vietnamese
          statuses.forEach(status -> {
               if (status.getStatus() != null) {
                    status.setStatus(normalizeHealthStatus(status.getStatus()));
               }
          });

          return statuses;
     }

     // 🔹 Tạo trạng thái mới cho cây
     @Transactional
     public PlantStatus createStatus(Long plantId, PlantStatus status) {
          Authentication auth = getAuth();
          if (auth == null) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
          }

          // Kiểm tra và lấy thông tin cây
          Plants plant = plantRepo.findByIdAndUser_Username(plantId, auth.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));

          // Validate dữ liệu đầu vào
          if (status.getStatus() == null || status.getStatus().trim().isEmpty()) {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
          }
          if (status.getDescription() == null || status.getDescription().trim().isEmpty()) {
               throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
          }

          // Set thông tin bổ sung
          status.setPlants(plant);
          status.setUpdateAt(LocalDateTime.now());
          return statusRepo.save(status);
     }

     // 🔹 Lấy trạng thái mới nhất của cây
     public PlantStatus getLatestStatus(Long plantId) {
          Authentication auth = getAuth();
          if (auth == null) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
          }

          // Admin có thể xem tất cả, user chỉ xem của mình
          if (!isAdmin()) {
               // Kiểm tra quyền truy cập cây
               plantRepo.findByIdAndUser_Username(plantId, auth.getName())
                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                                   "Access denied to this plant"));
          } else {
               // Admin: verify plant exists
               plantRepo.findById(plantId)
                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));
          }

          PlantStatus latestStatus = statusRepo.findByPlants_IdOrderByUpdateAtAsc(plantId)
                    .stream()
                    .reduce((first, second) -> second)
                    .orElse(null);

          // Normalize status to Vietnamese
          if (latestStatus != null && latestStatus.getStatus() != null) {
               latestStatus.setStatus(normalizeHealthStatus(latestStatus.getStatus()));
          }

          return latestStatus;
     }

     // 🔹 Cập nhật trạng thái
     @Transactional
     public PlantStatus updateStatus(Long statusId, PlantStatus updatedStatus) {
          Authentication auth = getAuth();
          if (auth == null) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
          }

          // Tìm status cũ
          PlantStatus existingStatus = statusRepo.findById(statusId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));

          // Kiểm tra quyền (thông qua plant ownership)
          if (!existingStatus.getPlants().getUser().getUsername().equals(auth.getName())) {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN);
          }

          // Cập nhật thông tin
          existingStatus.setStatus(updatedStatus.getStatus());
          existingStatus.setDescription(updatedStatus.getDescription());
          existingStatus.setImageurl(updatedStatus.getImageurl());
          existingStatus.setUpdateAt(LocalDateTime.now());
          return statusRepo.save(existingStatus);
     }

     // 🔹 Xóa trạng thái
     @Transactional
     public void deleteStatus(Long statusId) {
          Authentication auth = getAuth();
          if (auth == null) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
          }

          // Tìm status
          PlantStatus status = statusRepo.findById(statusId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Status not found"));

          // Kiểm tra quyền
          if (!status.getPlants().getUser().getUsername().equals(auth.getName())) {
               throw new ResponseStatusException(HttpStatus.FORBIDDEN);
          }

          statusRepo.delete(status);
     }

     // 🔹 Xóa nhiều trạng thái cùng lúc (bulk delete)
     @Transactional
     public void bulkDeleteStatuses(List<Long> ids) {
          Authentication auth = getAuth();
          if (auth == null) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
          }

          // Verify ownership for all statuses
          for (Long id : ids) {
               PlantStatus status = statusRepo.findById(id)
                         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                   "Status " + id + " not found"));

               if (!status.getPlants().getUser().getUsername().equals(auth.getName())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete status " + id);
               }
          }

          // Delete all if authorized
          statusRepo.deleteAllById(ids);
     }

     // 🔹 Thêm nhiều trạng thái cho một cây (bulk create)
     @Transactional
     public List<PlantStatus> bulkCreateStatuses(Long plantId, List<PlantStatus> statuses) {
          Authentication auth = getAuth();
          if (auth == null) {
               throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
          }

          // Kiểm tra quyền truy cập cây
          Plants plant = plantRepo.findByIdAndUser_Username(plantId, auth.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant not found"));

          LocalDateTime now = LocalDateTime.now();

          for (PlantStatus status : statuses) {
               // Validate
               if (status.getStatus() == null || status.getStatus().trim().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
               }
               if (status.getDescription() == null || status.getDescription().trim().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Description is required");
               }

               // Set thông tin
               status.setPlants(plant);
               status.setUpdateAt(now);
          }

          return statusRepo.saveAll(statuses);
     }
}