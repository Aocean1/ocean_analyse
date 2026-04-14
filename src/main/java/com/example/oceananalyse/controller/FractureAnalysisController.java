package com.example.oceananalyse.controller;

import com.example.oceananalyse.entity.FractureAnalysis;
import com.example.oceananalyse.service.FractureAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fracture-analysis")
public class FractureAnalysisController {

    @Autowired
    private FractureAnalysisService fractureAnalysisService;

    @PostMapping("/core/{coreSampleId}")
    public ResponseEntity<FractureAnalysis> createFractureAnalysis(
            @PathVariable Long coreSampleId,
            @RequestBody FractureAnalysis fractureAnalysis) {
        try {
            FractureAnalysis created = fractureAnalysisService.saveFractureAnalysis(coreSampleId, fractureAnalysis);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/core/{coreSampleId}")
    public ResponseEntity<FractureAnalysis> getFractureAnalysisByCoreSample(@PathVariable Long coreSampleId) {
        return fractureAnalysisService.getFractureAnalysisByCoreSampleId(coreSampleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FractureAnalysis> updateFractureAnalysis(
            @PathVariable Long id,
            @RequestBody FractureAnalysis fractureAnalysisDetails) {
        try {
            FractureAnalysis updated = fractureAnalysisService.updateFractureAnalysis(id, fractureAnalysisDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFractureAnalysis(@PathVariable Long id) {
        fractureAnalysisService.deleteFractureAnalysis(id);
        return ResponseEntity.noContent().build();
    }
}
