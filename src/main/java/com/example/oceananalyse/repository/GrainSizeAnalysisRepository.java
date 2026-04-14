package com.example.oceananalyse.repository;

import com.example.oceananalyse.entity.GrainSizeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GrainSizeAnalysisRepository extends JpaRepository<GrainSizeAnalysis, Long> {
    
    Optional<GrainSizeAnalysis> findByCoreSampleId(Long coreSampleId);
    
    void deleteByCoreSampleId(Long coreSampleId);
}
