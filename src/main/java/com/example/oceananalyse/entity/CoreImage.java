package com.example.oceananalyse.entity;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "core_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoreImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "core_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private CoreSample coreSample;

    @Column(name = "image_path", length = 255)
    private String imagePath;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "format", length = 50)
    private String format;

    @Column(name = "hole_count")
    private Integer holeCount;

    @Column(name = "avg_hole_size")
    private Double avgHoleSize;

    @Column(name = "porosity")
    private Double porosity;

    @Column(name = "grain_size")
    private Double grainSize;

    @Column(name = "grain_count")
    private Integer grainCount;

    @Column(name = "roundness")
    private Double roundness;

    @Column(name = "grain_shape", length = 20)
    private String grainShape;

    @Column(name = "sorting", length = 20)
    private String sorting;

    @Column(name = "fracture_count")
    private Integer fractureCount;

    @Column(name = "fracture_length")
    private Double fractureLength;

    @Column(name = "fracture_orientation", length = 20)
    private String fractureOrientation;

    @Column(name = "brightness")
    private Double brightness;

    @Column(name = "contrast")
    private Double contrast;

    @Column(name = "entropy")
    private Double entropy;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @PrePersist
    protected void onCreate() {
        uploadTime = LocalDateTime.now();
    }
}
