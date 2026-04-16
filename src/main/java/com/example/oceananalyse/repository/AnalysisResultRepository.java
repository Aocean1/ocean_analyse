package com.example.oceananalyse.repository;

import com.example.oceananalyse.entity.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
    List<AnalysisResult> findByCoreSampleId(Long coreId);
    Optional<AnalysisResult> findByCoreSampleIdAndAnalysisType(Long coreId, String analysisType);
}