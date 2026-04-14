package com.example.oceananalyse.controller;

import com.example.oceananalyse.entity.HoleAnalysis;
import com.example.oceananalyse.service.HoleAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hole-analysis")
public class HoleAnalysisController {

    @Autowired
    private HoleAnalysisService holeAnalysisService;

    @PostMapping("/core/{coreSampleId}")
    public ResponseEntity<HoleAnalysis> createHoleAnalysis(
            @PathVariable Long coreSampleId,
            @RequestBody HoleAnalysis holeAnalysis) {
        try {
            HoleAnalysis created = holeAnalysisService.saveHoleAnalysis(coreSampleId, holeAnalysis);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/core/{coreSampleId}")
    public ResponseEntity<HoleAnalysis> getHoleAnalysisByCoreSample(@PathVariable Long coreSampleId) {
        return holeAnalysisService.getHoleAnalysisByCoreSampleId(coreSampleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<HoleAnalysis> updateHoleAnalysis(
            @PathVariable Long id,
            @RequestBody HoleAnalysis holeAnalysisDetails) {
        try {
            HoleAnalysis updated = holeAnalysisService.updateHoleAnalysis(id, holeAnalysisDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHoleAnalysis(@PathVariable Long id) {
        holeAnalysisService.deleteHoleAnalysis(id);
        return ResponseEntity.noContent().build();
    }
}
