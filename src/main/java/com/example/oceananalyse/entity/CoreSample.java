package com.example.oceananalyse.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "core_sample")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sample_no", unique = true, nullable = false, length = 50)
    private String sampleNo;

    @Column(name = "basin_name", length = 100)
    private String basinName;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "depth_from", precision = 10, scale = 2)
    private Double depthFrom;

    @Column(name = "depth_to", precision = 10, scale = 2)
    private Double depthTo;

    @Column(name = "formation", length = 100)
    private String formation;

    @Column(name = "lithology", length = 100)
    private String lithology;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
