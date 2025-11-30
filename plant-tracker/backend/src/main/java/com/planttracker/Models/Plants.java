package com.planttracker.Models;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "plants", indexes = {
        @Index(name = "idx_plant_user_id", columnList = "user_id"),
        @Index(name = "idx_plant_type_id", columnList = "plant_type_id"),
        @Index(name = "idx_plant_create_date", columnList = "createDate")
})
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Plants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String species;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @Column(columnDefinition = "LONGTEXT")
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne
    @JoinColumn(name = "plant_type_id", nullable = false)
    private PlantTypes plantType;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();;

    // Relationships with cascade delete
    @OneToMany(mappedBy = "plants", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "plants", "hibernateLazyInitializer", "handler" })
    private List<PlantStatus> plantStatuses;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "plant", "hibernateLazyInitializer", "handler" })
    private List<PlantReport> plantReports;

    @OneToMany(mappedBy = "plant", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({ "plant", "hibernateLazyInitializer", "handler" })
    private List<CareSchedule> careSchedules;

    // Constructor với giá trị mặc định
    public Plants() {
        LocalDateTime now = LocalDateTime.now();
        this.createDate = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    // ✅ Gán tự động giá trị trước khi insert vào DB
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createDate = now;
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ====== Getter/Setter ======
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public PlantTypes getPlantType() {
        return plantType;
    }

    public void setPlantType(PlantTypes plantType) {
        this.plantType = plantType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<PlantStatus> getPlantStatuses() {
        return plantStatuses;
    }

    public void setPlantStatuses(List<PlantStatus> plantStatuses) {
        this.plantStatuses = plantStatuses;
    }

    public List<PlantReport> getPlantReports() {
        return plantReports;
    }

    public void setPlantReports(List<PlantReport> plantReports) {
        this.plantReports = plantReports;
    }

    public List<CareSchedule> getCareSchedules() {
        return careSchedules;
    }

    public void setCareSchedules(List<CareSchedule> careSchedules) {
        this.careSchedules = careSchedules;
    }
}
