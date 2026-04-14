package com.example.oceananalyse.repository;

import com.example.oceananalyse.entity.HoleAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HoleAnalysisRepository extends JpaRepository<HoleAnalysis, Long> {
    
    Optional<HoleAnalysis> findByCoreSampleId(Long coreSampleId);
    
    void deleteByCoreSampleId(Long coreSampleId);
}
