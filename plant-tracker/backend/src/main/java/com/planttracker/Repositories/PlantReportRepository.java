package com.planttracker.Repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import com.planttracker.Models.PlantReport;

public interface PlantReportRepository extends JpaRepository<PlantReport, Long> {

     List<PlantReport> findByPlant_IdOrderByDateAsc(Long plantId);

     Page<PlantReport> findByPlant_IdOrderByDateDesc(Long plantId, Pageable pageable);

     List<PlantReport> findByUser_UsernameOrderByDateDesc(String username);

     Page<PlantReport> findByUser_UsernameOrderByDateDesc(String username, Pageable pageable);

     // Delete methods for cascade delete
     @Modifying
     @Transactional
     void deleteByPlantId(Long plantId);
}
