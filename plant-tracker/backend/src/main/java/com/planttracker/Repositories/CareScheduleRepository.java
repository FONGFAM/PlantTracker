package com.planttracker.Repositories;

import java.time.LocalDateTime;
import java.util.List;

// 👇 Thêm import cho Phân trang
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.planttracker.Models.CareSchedule;

public interface CareScheduleRepository extends JpaRepository<CareSchedule, Long> {

     /**
      * TỐI ƯU 1 (Đã sửa cho Phân trang):
      * Lấy các lịch trình THEO plantId VÀ CHỈ NHỮNG CÁI CÒN KÍCH HOẠT (active).
      * Trả về Page thay vì List.
      */
     Page<CareSchedule> findByPlant_IdAndActiveTrue(Long plantId, Pageable pageable); // ✨ Đổi List -> Page, thêm
                                                                                      // Pageable

     /**
      * TỐI ƯU 2 (Đã sửa cho Phân trang):
      * Giống như hàm của bạn, nhưng cũng chỉ lấy các lịch còn KÍCH HOẠT.
      * Trả về Page thay vì List.
      */
     Page<CareSchedule> findByPlant_User_UsernameAndActiveTrue(String username, Pageable pageable); // ✨ Đổi List ->
                                                                                                    // Page, thêm
                                                                                                    // Pageable

     /**
      * BỔ SUNG 1 (Giữ nguyên - Thường không cần phân trang):
      * Tìm tất cả các lịch (còn active) đã "đến hạn".
      * Vẫn trả về List vì thường bạn muốn xử lý tất cả mục đến hạn cùng lúc.
      */
     @Query("SELECT s FROM CareSchedule s WHERE s.active = true AND s.nextAt <= :now")
     List<CareSchedule> findActiveSchedulesDue(@Param("now") LocalDateTime now);

     /**
      * BỔ SUNG 2 (Giữ nguyên - Thường không cần phân trang):
      * Tìm tất cả các lịch "đến hạn" CHO MỘT NGƯỜI DÙNG CỤ THỂ.
      * Vẫn trả về List.
      */
     @Query("SELECT s FROM CareSchedule s WHERE s.plant.user.username = :username AND s.active = true AND s.nextAt <= :now")
     List<CareSchedule> findActiveSchedulesDueForUser(@Param("username") String username,
               @Param("now") LocalDateTime now);

     // Hàm gốc (vẫn trả về List nếu bạn cần chúng không phân trang)
     List<CareSchedule> findByPlant_Id(Long plantId);

     List<CareSchedule> findByPlant_User_Username(String username);

     // JpaRepository đã cung cấp sẵn hàm findAll(Pageable pageable)
     // Page<CareSchedule> findAll(Pageable pageable); // ✨ Không cần khai báo lại
}