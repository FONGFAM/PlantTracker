package com.planttracker.Repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.planttracker.Models.PlantStatus;

public interface PlantStatusRepository extends JpaRepository<PlantStatus, Long> {
     List<PlantStatus> findByPlants_IdOrderByUpdateAtAsc(Long plantId);
}
