package com.planttracker.Repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import com.planttracker.Models.PlantStatus;

public interface PlantStatusRepository extends JpaRepository<PlantStatus, Long> {
     List<PlantStatus> findByPlants_IdOrderByUpdateAtAsc(Long plantId);

     // Delete methods for cascade delete
     @Modifying
     @Transactional
     void deleteByPlantsId(Long plantId);
}
