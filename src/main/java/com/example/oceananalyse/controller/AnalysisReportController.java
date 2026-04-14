package com.example.oceananalyse.controller;

import com.example.oceananalyse.entity.AnalysisReport;
import com.example.oceananalyse.service.AnalysisReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class AnalysisReportController {

    @Autowired
    private AnalysisReportService analysisReportService;

    @PostMapping("/generate/{coreSampleId}")
    public ResponseEntity<AnalysisReport> generateReport(@PathVariable Long coreSampleId) {
        try {
            AnalysisReport report = analysisReportService.generateReport(coreSampleId);
            return ResponseEntity.ok(report);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public List<AnalysisReport> getAllReports() {
        return analysisReportService.getAllReports();
    }

    @GetMapping("/core/{coreSampleId}")
    public List<AnalysisReport> getReportsByCoreSample(@PathVariable Long coreSampleId) {
        return analysisReportService.getReportsByCoreSampleId(coreSampleId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AnalysisReport> getReportById(@PathVariable Long id) {
        return analysisReportService.getReportById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        analysisReportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
