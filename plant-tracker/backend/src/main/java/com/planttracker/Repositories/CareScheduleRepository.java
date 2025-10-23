package com.planttracker.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planttracker.Models.CareSchedules;

public interface CareScheduleRepository extends JpaRepository<CareSchedules, Long> {
     List<CareSchedules> findByPlant_Id(Long plantId);

     List<CareSchedules> findByPlant_User_Username(String username);
}
