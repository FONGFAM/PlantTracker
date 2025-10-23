package com.planttracker.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.planttracker.Models.Users;

public interface  UserRepository extends JpaRepository<Users, Long> {
     Optional<Users> findByUsername(String username);
     Optional<Users> findByEmail(String email);
     Boolean existsByUsername(String username);
     Boolean existsByEmail(String email);

}
