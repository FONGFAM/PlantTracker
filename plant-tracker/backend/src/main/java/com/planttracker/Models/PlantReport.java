package com.planttracker.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "plant_reports", indexes = {
          @Index(name = "idx_report_plant_id", columnList = "plant_id"),
          @Index(name = "idx_report_date", columnList = "date")
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
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

     @Column(columnDefinition = "LONGTEXT")
     private String imageUrl; // Base64 encoded image can be very long

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "plant_id", nullable = false)
     @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "user", "plantReports" })
     private Plants plant;

     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "user_id", nullable = false)
     @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "password", "plants", "roles" })
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
