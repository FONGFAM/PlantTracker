package com.planttracker.Models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.*;

@Entity
@Table(name = "care_schedules", indexes = {
          @Index(name = "idx_schedule_plant_id", columnList = "plant_id"),
          @Index(name = "idx_schedule_next_at", columnList = "nextAt"),
          @Index(name = "idx_schedule_type", columnList = "type")
})
public class CareSchedule {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @ManyToOne
     @JoinColumn(name = "plant_id", nullable = false)
     private Plants plant;

     @Column(nullable = false)
     private String type; // e.g., WATER, FERTILIZE, LIGHT

     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
     private LocalDateTime nextAt;

     private String frequency; // optional human-readable frequency

     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
     private LocalDateTime lastPerformedAt;

     private boolean active = true;

     // getters/setters
     public Long getId() {
          return id;
     }

     public void setId(Long id) {
          this.id = id;
     }

     public Plants getPlant() {
          return plant;
     }

     public void setPlant(Plants plant) {
          this.plant = plant;
     }

     public String getType() {
          return type;
     }

     public void setType(String type) {
          this.type = type;
     }

     public LocalDateTime getNextAt() {
          return nextAt;
     }

     public void setNextAt(LocalDateTime nextAt) {
          this.nextAt = nextAt;
     }

     public String getFrequency() {
          return frequency;
     }

     public void setFrequency(String frequency) {
          this.frequency = frequency;
     }

     public LocalDateTime getLastPerformedAt() {
          return lastPerformedAt;
     }

     public void setLastPerformedAt(LocalDateTime lastPerformedAt) {
          this.lastPerformedAt = lastPerformedAt;
     }

     public boolean isActive() {
          return active;
     }

     public void setActive(boolean active) {
          this.active = active;
     }
}
