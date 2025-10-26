package com.planttracker.Repositories;

import java.time.LocalDateTime; // Cần import
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Cần import
import org.springframework.data.repository.query.Param; // Cần import

import com.planttracker.Models.CareSchedule;

public interface CareScheduleRepository extends JpaRepository<CareSchedule, Long> {

     /**
      * * TỐI ƯU 1:
      * Lấy các lịch trình THEO plantId VÀ CHỈ NHỮNG CÁI CÒN KÍCH HOẠT (active).
      * Service của bạn cần điều này (trong hàm getActiveByPlantId).
      */
     List<CareSchedule> findByPlant_IdAndActiveTrue(Long plantId);

     /**
      * TỐI ƯU 2:
      * Giống như hàm của bạn, nhưng cũng chỉ lấy các lịch còn KÍCH HOẠT.
      * Người dùng thường không muốn thấy các lịch họ đã xóa.
      */
     List<CareSchedule> findByPlant_User_UsernameAndActiveTrue(String username);

     /**
      * BỔ SUNG 1 (Rất quan trọng):
      * Tìm tất cả các lịch (còn active) đã "đến hạn".
      * Dùng để chạy một tác vụ nền (background job) hoặc gửi thông báo.
      */
     @Query("SELECT s FROM CareSchedule s WHERE s.active = true AND s.nextAt <= :now")
     List<CareSchedule> findActiveSchedulesDue(@Param("now") LocalDateTime now);

     /**
      * BỔ SUNG 2 (Hữu ích):
      * Tìm tất cả các lịch "đến hạn" CHO MỘT NGƯỜI DÙNG CỤ THỂ.
      * Rất tốt cho việc hiển thị "Việc cần làm hôm nay" trên dashboard.
      */
     @Query("SELECT s FROM CareSchedule s WHERE s.plant.user.username = :username AND s.active = true AND s.nextAt <= :now")
     List<CareSchedule> findActiveSchedulesDueForUser(@Param("username") String username,
               @Param("now") LocalDateTime now);

     // Hàm gốc của bạn (vẫn giữ lại nếu bạn cần lấy TẤT CẢ, kể cả cái đã de-active)
     List<CareSchedule> findByPlant_Id(Long plantId);

     List<CareSchedule> findByPlant_User_Username(String username);
}