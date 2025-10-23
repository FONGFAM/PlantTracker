package com.planttracker.dto;

public class PlantAnalyticsDTO {
     private Long plantId;
     private String plantName;
     private Double avgHeight;
     private Double avgHumidity;
     private Double avgTemperature;
     private Long totalReports;
     private Long totalWatered;
     private Long totalFertilized;
     private Long totalSickDays;

     // Constructors
     public PlantAnalyticsDTO() {
     }

     public PlantAnalyticsDTO(Long plantId, String plantName) {
          this.plantId = plantId;
          this.plantName = plantName;
     }

     // Getters & Setters
     public Long getPlantId() {
          return plantId;
     }

     public void setPlantId(Long plantId) {
          this.plantId = plantId;
     }

     public String getPlantName() {
          return plantName;
     }

     public void setPlantName(String plantName) {
          this.plantName = plantName;
     }

     public Double getAvgHeight() {
          return avgHeight;
     }

     public void setAvgHeight(Double avgHeight) {
          this.avgHeight = avgHeight;
     }

     public Double getAvgHumidity() {
          return avgHumidity;
     }

     public void setAvgHumidity(Double avgHumidity) {
          this.avgHumidity = avgHumidity;
     }

     public Double getAvgTemperature() {
          return avgTemperature;
     }

     public void setAvgTemperature(Double avgTemperature) {
          this.avgTemperature = avgTemperature;
     }

     public Long getTotalReports() {
          return totalReports;
     }

     public void setTotalReports(Long totalReports) {
          this.totalReports = totalReports;
     }

     public Long getTotalWatered() {
          return totalWatered;
     }

     public void setTotalWatered(Long totalWatered) {
          this.totalWatered = totalWatered;
     }

     public Long getTotalFertilized() {
          return totalFertilized;
     }

     public void setTotalFertilized(Long totalFertilized) {
          this.totalFertilized = totalFertilized;
     }

     public Long getTotalSickDays() {
          return totalSickDays;
     }

     public void setTotalSickDays(Long totalSickDays) {
          this.totalSickDays = totalSickDays;
     }
}
