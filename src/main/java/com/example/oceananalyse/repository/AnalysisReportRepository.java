package com.example.oceananalyse.repository;

import com.example.oceananalyse.entity.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, Long> {
    
    List<AnalysisReport> findByCoreSampleId(Long coreSampleId);
    
    List<AnalysisReport> findAllByOrderByGeneratedAtDesc();
}
