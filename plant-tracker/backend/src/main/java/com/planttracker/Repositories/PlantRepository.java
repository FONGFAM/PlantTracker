package com.planttracker.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.planttracker.Models.Plants;

public interface PlantRepository extends JpaRepository<Plants, Long> {
	// Non-paginated methods
	List<Plants> findByUser_Username(String username);

	Optional<Plants> findByIdAndUser_Username(Long id, String username);

	boolean existsByIdAndUser_Username(Long id, String username);

	// Paginated methods
	Page<Plants> findByUser_Username(String username, Pageable pageable);
}
