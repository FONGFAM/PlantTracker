package com.planttracker.Controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.planttracker.Models.Plants;
import com.planttracker.Services.PlantService;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private static final Logger logger = LoggerFactory.getLogger(PlantController.class);

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    // 🔹 Lấy tất cả cây với phân trang
    @GetMapping
    public ResponseEntity<Page<Plants>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "createDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Plants> plantsPage = plantService.listPlants(pageable);
        return ResponseEntity.ok(plantsPage);
    }

    // 🔹 Lấy chi tiết 1 cây
    @GetMapping("/{id}")
    public ResponseEntity<Plants> getById(@PathVariable Long id) {
        Plants plant = plantService.getPlant(id);
        return ResponseEntity.ok(plant);
    }

    // 🔹 Thêm cây mới
    @PostMapping
    public ResponseEntity<?> addPlant(@RequestBody Plants plant) {
        try {
            // Log để debug
            logger.debug("Received plant: {}", plant.getName());
            logger.debug("ImageUrl: {}", plant.getImageUrl() != null
                    ? plant.getImageUrl().substring(0, Math.min(50, plant.getImageUrl().length()))
                    : "null");
            logger.debug("PlantType: {}", plant.getPlantType() != null ? plant.getPlantType().getId() : "null");
            logger.debug("User: {}", plant.getUser() != null ? plant.getUser().getUsername() : "null");

            // Validate dữ liệu đầu vào
            if (plant.getName() == null || plant.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên cây không được để trống");
            }
            if (plant.getImageUrl() == null || plant.getImageUrl().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("URL hình ảnh không được để trống");
            }
            if (plant.getPlantType() == null || plant.getPlantType().getId() == null) {
                return ResponseEntity.badRequest().body("Loại cây không được để trống");
            }

            // Tạo mới plant (createDate sẽ được set tự động trong service)
            Plants created = plantService.createPlant(plant);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            logger.error("Error creating plant for user: {}",
                    plant.getUser() != null ? plant.getUser().getId() : "unknown", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("❌ Lỗi khi thêm cây: " + e.getMessage());
        }
    }

    // 🔹 Cập nhật cây
    @PutMapping("/{id}")
    public ResponseEntity<Plants> updatePlant(@PathVariable Long id, @RequestBody Plants plant) {
        Plants updated = plantService.updatePlant(id, plant);
        return ResponseEntity.ok(updated);
    }

    // 🔹 Xóa cây
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        plantService.deletePlant(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Xóa nhiều cây cùng lúc (bulk delete)
    @DeleteMapping("/bulk")
    public ResponseEntity<String> bulkDeletePlants(@RequestBody List<Long> ids) {
        plantService.bulkDeletePlants(ids);
        return ResponseEntity.ok("Đã xóa " + ids.size() + " cây");
    }

    // 🔹 Thêm nhiều cây cùng lúc (bulk create)
    @PostMapping("/bulk")
    public ResponseEntity<List<Plants>> bulkCreatePlants(@RequestBody List<Plants> plants) {
        logger.info("Bulk create endpoint called with {} plants", plants.size());
        try {
            List<Plants> created = plantService.bulkCreatePlants(plants);
            logger.info("Bulk create successful: {} plants created", created.size());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (ResponseStatusException e) {
            logger.error("ResponseStatusException during bulk create: {}", e.getReason(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error during bulk plant creation", e);
            throw e;
        }
    }
}
