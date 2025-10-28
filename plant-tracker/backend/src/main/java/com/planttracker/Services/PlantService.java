package com.planttracker.Services;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.planttracker.Models.Plants;
import com.planttracker.Models.Users;
import com.planttracker.Models.PlantTypes;
import com.planttracker.Repositories.PlantRepository;
import com.planttracker.Repositories.UserRepository;
import com.planttracker.Repositories.PlantTypeRepository;

@Service
public class PlantService {

    private static final Logger logger = LoggerFactory.getLogger(PlantService.class);

    private final PlantRepository repo;
    private final UserRepository userRepo;
    private final PlantTypeRepository plantTypeRepo;

    public PlantService(PlantRepository repo, UserRepository userRepo, PlantTypeRepository plantTypeRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.plantTypeRepo = plantTypeRepo;
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

    // 🔹 Lấy danh sách cây với phân trang (admin thấy tất cả, user chỉ thấy cây của
    // mình)
    public Page<Plants> listPlants(Pageable pageable) {
        Authentication auth = getAuth();
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

        if (isAdmin())
            return repo.findAll(pageable);
        return repo.findByUser_Username(auth.getName(), pageable);
    }

    // 🔹 Lấy danh sách cây không phân trang (giữ cho backward compatibility)
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
        } else {
            // Nếu là admin và không có user, cần set một user mặc định
            if (p.getUser() == null) {
                Users owner = userRepo.findByUsername(auth.getName())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                p.setUser(owner);
            }
        }

        // Validate và load PlantType từ database
        if (p.getPlantType() == null || p.getPlantType().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plant type is required");
        }

        // 🔥 Load PlantType entity từ database
        PlantTypes plantType = plantTypeRepo.findById(p.getPlantType().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Plant type with ID " + p.getPlantType().getId() + " not found"));
        p.setPlantType(plantType);

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

    // 🔹 Xóa nhiều cây cùng lúc (bulk delete)
    @Transactional
    public void bulkDeletePlants(List<Long> ids) {
        Authentication auth = getAuth();
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

        // Verify ownership for all plants
        for (Long id : ids) {
            Plants existing = repo.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plant " + id + " not found"));

            if (!isAdmin() && !existing.getUser().getUsername().equals(auth.getName())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot delete plant " + id);
            }
        }

        // Delete all if authorized
        repo.deleteAllById(ids);
    }

    // 🔹 Thêm nhiều cây cùng lúc (bulk create)
    @Transactional
    public List<Plants> bulkCreatePlants(List<Plants> plants) {
        Authentication auth = getAuth();
        logger.debug("Bulk create - Auth: {}", auth != null ? auth.getName() : "null");
        logger.debug("Bulk create - IsAdmin: {}", isAdmin());
        logger.info("Bulk create - Plants count: {}", plants.size());

        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You must log in");

        LocalDateTime now = LocalDateTime.now();

        for (Plants p : plants) {
            logger.debug("Processing plant: {}", p.getName());

            // Set user if not provided (for non-admin)
            if (!isAdmin()) {
                Users owner = userRepo.findByUsername(auth.getName())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                p.setUser(owner);
                logger.debug("Set owner (non-admin): {}", owner.getUsername());
            } else {
                if (p.getUser() == null) {
                    Users owner = userRepo.findByUsername(auth.getName())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                    p.setUser(owner);
                    logger.debug("Set owner (admin): {}", owner.getUsername());
                }
            }

            // Validate và load PlantType
            if (p.getPlantType() == null || p.getPlantType().getId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plant type is required");
            }

            PlantTypes plantType = plantTypeRepo.findById(p.getPlantType().getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Plant type with ID " + p.getPlantType().getId() + " not found"));
            p.setPlantType(plantType);
            logger.debug("Set plant type: {}", plantType.getTypeName());

            // Set timestamps
            p.setCreateDate(now);
            p.setCreatedAt(now);
            p.setUpdatedAt(now);
        }

        logger.info("Saving {} plants...", plants.size());
        List<Plants> saved = repo.saveAll(plants);
        logger.info("Saved successfully!");
        return saved;
    }
}
