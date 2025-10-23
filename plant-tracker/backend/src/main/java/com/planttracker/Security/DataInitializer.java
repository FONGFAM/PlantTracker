package com.planttracker.Security;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.planttracker.Models.Role;
import com.planttracker.Models.Users;
import com.planttracker.Repositories.RoleRepository;
import com.planttracker.Repositories.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(UserRepository userRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Nếu chưa có role nào, tạo mặc định
        if (roleRepo.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepo.saveAll(List.of(adminRole, userRole));
        }

        // Tạo user demo thông thường (không phải admin)
        Role userRole = roleRepo.findByName("ROLE_USER").orElseThrow();

        if (!userRepo.existsByUsername("demo")) {
            Users demoUser = new Users();
            demoUser.setUsername("demo");
            demoUser.setPassword(passwordEncoder.encode("123456"));
            demoUser.setEmail("demo@plant.com");
            demoUser.setCreatedAt(LocalDateTime.now());
            // Assign only USER role
            demoUser.setRoles(List.of(userRole));
            try {
                userRepo.save(demoUser);
                System.out.println("✅ Demo user created: demo / 123456");
            } catch (DataIntegrityViolationException ex) {
                logger.warn("Demo user already exists or constraint violated while creating demo user: {}",
                        ex.getMessage());
            }
        }
    }
}
