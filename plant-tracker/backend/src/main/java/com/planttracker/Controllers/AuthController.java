package com.planttracker.Controllers;

import com.planttracker.Models.Role;
import com.planttracker.Models.Users;
import com.planttracker.Repositories.RoleRepository;
import com.planttracker.Repositories.UserRepository;
import com.planttracker.Security.JwtUtils;
import com.planttracker.Services.CustomUserDetailsService;
import com.planttracker.dto.LoginRequest;
import com.planttracker.dto.RegisterRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // =================== LOGIN ===================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login attempt for user: {}", loginRequest.getUsername());

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            logger.info("Authentication successful for: {}", loginRequest.getUsername());

            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
            String token = jwtUtils.generateJwtToken(userDetails);
            logger.debug("JWT token generated for user: {}", loginRequest.getUsername());

            // Lấy thông tin user đầy đủ
            Users user = userRepo.findByUsername(loginRequest.getUsername()).orElse(null);

            if (user != null) {
                logger.info("User found: {} with {} roles", user.getUsername(), user.getRoles().size());
                // Tạo response với đầy đủ thông tin
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("user", user);
                response.put("roles", user.getRoles());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("User not found in database: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

        } catch (BadCredentialsException e) {
            logger.warn("Bad credentials for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            logger.error("Login error for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    // =================== REGISTER ===================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Register attempt for user: {}", registerRequest.getUsername());

            // Kiểm tra trùng username/email
            if (userRepo.existsByUsername(registerRequest.getUsername())) {
                logger.warn("Username already exists: {}", registerRequest.getUsername());
                return ResponseEntity.badRequest().body("Username is already taken");
            }
            if (userRepo.existsByEmail(registerRequest.getEmail())) {
                logger.warn("Email already exists: {}", registerRequest.getEmail());
                return ResponseEntity.badRequest().body("Email is already in use");
            }

            // Tạo user mới
            Users user = new Users();
            user.setUsername(registerRequest.getUsername());
            user.setEmail(registerRequest.getEmail());

            // Mã hoá mật khẩu
            String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
            user.setPassword(encodedPassword);
            logger.debug("Password encoded successfully for user: {}", registerRequest.getUsername());

            // Set createdAt
            user.setCreatedAt(LocalDateTime.now());

            // Gán role mặc định ROLE_USER
            Role userRole = roleRepo.findByName("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            user.setRoles(List.of(userRole));
            logger.info("Role assigned: {}", userRole.getName());

            // Lưu vào database
            Users savedUser = userRepo.save(user);
            logger.info("User registered successfully: {} (ID: {})", savedUser.getUsername(), savedUser.getId());

            return ResponseEntity.ok("User created successfully!");
        } catch (Exception e) {
            logger.error("Registration failed for username: {}", registerRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logged out successfully!");
    }
}
