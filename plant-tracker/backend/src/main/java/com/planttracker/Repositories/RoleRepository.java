package com.planttracker.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planttracker.Models.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
     
}
