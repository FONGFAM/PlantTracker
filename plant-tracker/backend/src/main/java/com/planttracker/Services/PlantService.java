package com.planttracker.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.planttracker.Models.Plants;
import com.planttracker.Models.Users;
import com.planttracker.Repositories.PlantRepository;
import com.planttracker.Repositories.UserRepository;

@Service
public class PlantService {

    private final PlantRepository repo;
    private final UserRepository userRepo;

    public PlantService(PlantRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    // 🔹 Lấy Authentication hiện tại từ SecurityContext
    private Authentication getAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    // 🔹 Kiểm tra quyền admin
    private boolean isAdmin() {
        Authentication auth = getAuth();
        if (auth == null || auth.getAuthorities() == null)
            return false;

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_ADMIN"));
    }

    // 🔹 Lấy danh sách cây (admin thấy tất cả, user chỉ thấy cây của mình)
    public List<Plants> listPlants() {
        Authentication auth = getAuth();
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

        if (isAdmin())
            return repo.findAll();
        return repo.findByUser_Username(auth.getName());
    }

    // 🔹 Lấy chi tiết 1 cây
    public Plants getPlant(Long id) {
        Authentication auth = getAuth();
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

        if (isAdmin()) {
            return repo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        }
        return repo.findByIdAndUser_Username(id, auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    // 🔹 Tạo cây mới
    public Plants createPlant(Plants p) {
        Authentication auth = getAuth();
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

        // Set user nếu không phải admin
        if (!isAdmin()) {
            Users owner = userRepo.findByUsername(auth.getName())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
            p.setUser(owner);
        }

        // Validate required fields
        if (p.getPlantType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plant type is required");
        }

        // Set all timestamps to current time
        LocalDateTime now = LocalDateTime.now();
        p.setCreateDate(now);
        p.setCreatedAt(now);
        p.setUpdatedAt(now);

        return repo.save(p);
    }

    // 🔹 Cập nhật cây
    public Plants updatePlant(Long id, Plants p) {
        Authentication auth = getAuth();
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

        Plants existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAdmin() && !existing.getUser().getUsername().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        existing.setName(p.getName());
        existing.setSpecies(p.getSpecies());
        existing.setDescription(p.getDescription());
        existing.setImageUrl(p.getImageUrl());
        existing.setCreateDate(p.getCreateDate());
        existing.setPlantType(p.getPlantType());
        existing.setUpdatedAt(LocalDateTime.now());

        return repo.save(existing);
    }

    // 🔹 Xóa cây
    public void deletePlant(Long id) {
        Authentication auth = getAuth();
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

        Plants existing = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAdmin() && !existing.getUser().getUsername().equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        repo.deleteById(id);
    }
}
