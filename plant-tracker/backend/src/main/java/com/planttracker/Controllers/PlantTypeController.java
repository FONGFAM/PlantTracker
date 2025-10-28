package com.planttracker.Controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.planttracker.Models.PlantTypes;
import com.planttracker.Services.PlantTypeService;

@RestController
@RequestMapping("/api/plant-types")
public class PlantTypeController {

     private final PlantTypeService service;

     public PlantTypeController(PlantTypeService service) {
          this.service = service;
     }

     @GetMapping
     public ResponseEntity<Page<PlantTypes>> list(
               @RequestParam(defaultValue = "0") int page,
               @RequestParam(defaultValue = "4") int size,
               @RequestParam(defaultValue = "typeName") String sortBy,
               @RequestParam(defaultValue = "asc") String sortDir) {

          Sort sort = sortDir.equalsIgnoreCase("asc")
                    ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();

          Pageable pageable = PageRequest.of(page, size, sort);
          return ResponseEntity.ok(service.list(pageable));
     }

     @GetMapping("/{id}")
     public ResponseEntity<PlantTypes> get(@PathVariable Long id) {
          return ResponseEntity.ok(service.get(id));
     }

     @PostMapping
     public ResponseEntity<PlantTypes> create(@RequestBody PlantTypes payload) {
          return ResponseEntity.ok(service.create(payload));
     }

     @PutMapping("/{id}")
     public ResponseEntity<PlantTypes> update(@PathVariable Long id, @RequestBody PlantTypes payload) {
          return ResponseEntity.ok(service.update(id, payload));
     }

     @DeleteMapping("/{id}")
     public ResponseEntity<Void> delete(@PathVariable Long id) {
          service.delete(id);
          return ResponseEntity.noContent().build();
     }
}
