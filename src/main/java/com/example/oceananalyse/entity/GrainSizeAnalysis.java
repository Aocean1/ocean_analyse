package com.example.oceananalyse.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "grain_size_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrainSizeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_sample_id", nullable = false)
    private CoreSample coreSample;

    @Column(name = "avg_grain_size", precision = 10, scale = 4)
    private Double avgGrainSize;

    @Column(name = "median_size", precision = 10, scale = 4)
    private Double medianSize;

    @Column(name = "sorting_coefficient", precision = 10, scale = 4)
    private Double sortingCoefficient;

    @Column(name = "skewness", precision = 10, scale = 4)
    private Double skewness;

    @Column(name = "kurtosis", precision = 10, scale = 4)
    private Double kurtosis;

    @Column(name = "grain_type", length = 100)
    private String grainType;

    @Column(name = "roundness", length = 50)
    private String roundness;

    @Column(name = "sorting_degree", length = 50)
    private String sortingDegree;

    @Column(name = "analysis_method", length = 100)
    private String analysisMethod;

    @Column(name = "analysis_note", columnDefinition = "TEXT")
    private String analysisNote;
}
