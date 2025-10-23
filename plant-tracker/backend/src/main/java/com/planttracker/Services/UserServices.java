package com.planttracker.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Collections;

import com.planttracker.Models.Users;
import com.planttracker.Repositories.RoleRepository;
import com.planttracker.Repositories.UserRepository;
import com.planttracker.Models.Role;

@Service
public class UserServices {
     @Autowired
     private UserRepository userRepo;
     @Autowired
     private RoleRepository roleRepo;
     @Autowired
     private PasswordEncoder passwordEncoder;

     public Users RegisterUser(String username, String email, String password) {

          if (userRepo.existsByUsername(username)) {
               throw new RuntimeException("Username is already taken");
          }
          if (userRepo.existsByEmail(email)) {
               throw new RuntimeException("Email is already in use");
          }

          Users user = new Users();
          user.setUsername(username);
          user.setEmail(email);
          user.setPassword(passwordEncoder.encode(password));
          user.setCreatedAt(LocalDateTime.now());

          Role userRole = roleRepo.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
          user.setRoles(Collections.singletonList(userRole));

          return userRepo.save(user);

     }

     public Users findByUsername(String username) {
          return userRepo.findByUsername(username).orElse(null);
     }
}
