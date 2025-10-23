package com.planttracker.Controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.planttracker.Models.Plants;
import com.planttracker.Services.PlantService;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    // 🔹 Lấy tất cả cây
    @GetMapping
    public List<Plants> getAll() {
        return plantService.listPlants();
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
            // Validate dữ liệu đầu
            // vàobackend/src/main/java/com/planttracker/Controllers/PlantController.java
            if (plant.getName() == null || plant.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên cây không được để trống");
            }
            if (plant.getImageUrl() == null || plant.getImageUrl().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("URL hình ảnh không được để trống");
            }
            if (plant.getPlantType() == null) {
                return ResponseEntity.badRequest().body("Loại cây không được để trống");
            }

            // Tạo mới plant (createDate sẽ được set tự động trong service)
            Plants created = plantService.createPlant(plant);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
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
}
