package com.planttracker.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.planttracker.Models.PlantTypes;

public interface PlantTypeRepository extends JpaRepository<PlantTypes, Long> {

}
