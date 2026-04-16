package com.example.oceananalyse.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "analysis_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_id", nullable = false)
    private CoreSample coreSample;

    @Column(name = "analysis_type", length = 20)
    private String analysisType;

    @Column(name = "hole_area")
    private Float holeArea;

    @Column(name = "hole_density")
    private Float holeDensity;

    @Column(name = "hole_diameter_avg")
    private Float holeDiameterAvg;

    @Column(name = "grain_size_avg")
    private Float grainSizeAvg;

    @Column(name = "sorting_coefficient")
    private Float sortingCoefficient;

    @Column(name = "skewness")
    private Float skewness;

    @Column(name = "kurtosis")
    private Float kurtosis;

    @Column(name = "fracture_density")
    private Float fractureDensity;

    @Column(name = "fracture_length_avg")
    private Float fractureLengthAvg;

    @Column(name = "fracture_width_avg")
    private Float fractureWidthAvg;

    @Column(name = "porosity")
    private Float porosity;

    @Column(name = "permeability")
    private Float permeability;
}