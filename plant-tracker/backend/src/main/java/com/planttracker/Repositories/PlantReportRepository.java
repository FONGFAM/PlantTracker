package com.planttracker.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.planttracker.Models.PlantReport;

public interface PlantReportRepository extends JpaRepository<PlantReport, Long> {

     List<PlantReport> findByPlant_IdOrderByDateAsc(Long plantId);

     List<PlantReport> findByUser_UsernameOrderByDateDesc(String username);
}
