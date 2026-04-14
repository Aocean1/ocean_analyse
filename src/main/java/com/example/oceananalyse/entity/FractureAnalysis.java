package com.example.oceananalyse.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "fracture_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FractureAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_sample_id", nullable = false)
    private CoreSample coreSample;

    @Column(name = "fracture_density", precision = 10, scale = 4)
    private Double fractureDensity;

    @Column(name = "avg_length", precision = 10, scale = 4)
    private Double avgLength;

    @Column(name = "avg_width", precision = 10, scale = 4)
    private Double avgWidth;

    @Column(name = "fracture_orientation", length = 100)
    private String fractureOrientation;

    @Column(name = "fracture_type", length = 50)
    private String fractureType;

    @Column(name = "filling_status", length = 50)
    private String fillingStatus;

    @Column(name = "connectivity", length = 50)
    private String connectivity;

    @Column(name = "analysis_method", length = 100)
    private String analysisMethod;

    @Column(name = "analysis_note", columnDefinition = "TEXT")
    private String analysisNote;
}
