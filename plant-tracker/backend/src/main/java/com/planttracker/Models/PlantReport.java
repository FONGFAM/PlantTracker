package com.planttracker.Models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "plant_reports")
public class PlantReport {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     private LocalDate date;

     private Double height; // cm
     private Double humidity; // %
     private Double temperature; // °C

     @Column(length = 100)
     private String healthStatus; // e.g. Good, Warning, Sick

     @Column(columnDefinition = "TEXT")
     private String note;

     @Column(length = 255)
     private String imageUrl; // optional: image recorded

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "plant_id", nullable = false)
     private Plants plant;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "user_id", nullable = false)
     private Users user;

     // constructors
     public PlantReport() {
     }

     // getters / setters
     public Long getId() {
          return id;
     }

     public void setId(Long id) {
          this.id = id;
     }

     public LocalDate getDate() {
          return date;
     }

     public void setDate(LocalDate date) {
          this.date = date;
     }

     public Double getHeight() {
          return height;
     }

     public void setHeight(Double height) {
          this.height = height;
     }

     public Double getHumidity() {
          return humidity;
     }

     public void setHumidity(Double humidity) {
          this.humidity = humidity;
     }

     public Double getTemperature() {
          return temperature;
     }

     public void setTemperature(Double temperature) {
          this.temperature = temperature;
     }

     public String getHealthStatus() {
          return healthStatus;
     }

     public void setHealthStatus(String healthStatus) {
          this.healthStatus = healthStatus;
     }

     public String getNote() {
          return note;
     }

     public void setNote(String note) {
          this.note = note;
     }

     public String getImageUrl() {
          return imageUrl;
     }

     public void setImageUrl(String imageUrl) {
          this.imageUrl = imageUrl;
     }

     public Plants getPlant() {
          return plant;
     }

     public void setPlant(Plants plant) {
          this.plant = plant;
     }

     public Users getUser() {
          return user;
     }

     public void setUser(Users user) {
          this.user = user;
     }
}
