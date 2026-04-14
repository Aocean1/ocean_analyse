package com.example.oceananalyse.repository;

import com.example.oceananalyse.entity.FractureAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FractureAnalysisRepository extends JpaRepository<FractureAnalysis, Long> {
    
    Optional<FractureAnalysis> findByCoreSampleId(Long coreSampleId);
    
    void deleteByCoreSampleId(Long coreSampleId);
}
