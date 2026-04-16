package com.example.oceananalyse.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "core_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreSample {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "core_id")
    private Long id;

    @Column(name = "sample_no", unique = true, nullable = false, length = 50)
    private String sampleNo;

    @Column(name = "basin_name", length = 50)
    private String basinName;

    @Column(name = "stratum", length = 30)
    private String stratum;

    @Column(name = "lithology", length = 30)
    private String lithology;

    @Column(name = "sedimentary_structure", length = 20)
    private String sedimentaryStructure;

    @Column(name = "location", length = 200)
    private String location;

    @Column(name = "depth_from", precision = 10, scale = 2)
    private Double depthFrom;

    @Column(name = "depth_to", precision = 10, scale = 2)
    private Double depthTo;

    @Column(name = "formation", length = 100)
    private String formation;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

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
