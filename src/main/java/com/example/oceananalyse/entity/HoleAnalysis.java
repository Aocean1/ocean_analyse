package com.example.oceananalyse.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "hole_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoleAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_sample_id", nullable = false)
    private CoreSample coreSample;

    @Column(name = "hole_density", precision = 10, scale = 4)
    private Double holeDensity;

    @Column(name = "avg_diameter", precision = 10, scale = 4)
    private Double avgDiameter;

    @Column(name = "max_diameter", precision = 10, scale = 4)
    private Double maxDiameter;

    @Column(name = "min_diameter", precision = 10, scale = 4)
    private Double minDiameter;

    @Column(name = "hole_shape", length = 50)
    private String holeShape;

    @Column(name = "hole_type", length = 50)
    private String holeType;

    @Column(name = "porosity", precision = 10, scale = 4)
    private Double porosity;

    @Column(name = "analysis_method", length = 100)
    private String analysisMethod;

    @Column(name = "analysis_note", columnDefinition = "TEXT")
    private String analysisNote;
}
