package com.example.oceananalyse.controller;

import com.example.oceananalyse.entity.GrainSizeAnalysis;
import com.example.oceananalyse.service.GrainSizeAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grain-size-analysis")
public class GrainSizeAnalysisController {

    @Autowired
    private GrainSizeAnalysisService grainSizeAnalysisService;

    @PostMapping("/core/{coreSampleId}")
    public ResponseEntity<GrainSizeAnalysis> createGrainSizeAnalysis(
            @PathVariable Long coreSampleId,
            @RequestBody GrainSizeAnalysis grainSizeAnalysis) {
        try {
            GrainSizeAnalysis created = grainSizeAnalysisService.saveGrainSizeAnalysis(coreSampleId, grainSizeAnalysis);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/core/{coreSampleId}")
    public ResponseEntity<GrainSizeAnalysis> getGrainSizeAnalysisByCoreSample(@PathVariable Long coreSampleId) {
        return grainSizeAnalysisService.getGrainSizeAnalysisByCoreSampleId(coreSampleId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GrainSizeAnalysis> updateGrainSizeAnalysis(
            @PathVariable Long id,
            @RequestBody GrainSizeAnalysis grainSizeAnalysisDetails) {
        try {
            GrainSizeAnalysis updated = grainSizeAnalysisService.updateGrainSizeAnalysis(id, grainSizeAnalysisDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGrainSizeAnalysis(@PathVariable Long id) {
        grainSizeAnalysisService.deleteGrainSizeAnalysis(id);
        return ResponseEntity.noContent().build();
    }
}
