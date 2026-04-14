package com.example.oceananalyse.controller;

import com.example.oceananalyse.entity.CoreSample;
import com.example.oceananalyse.service.CoreSampleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/core-samples")
public class CoreSampleController {

    @Autowired
    private CoreSampleService coreSampleService;

    @GetMapping
    public List<CoreSample> getAllCoreSamples() {
        return coreSampleService.getAllCoreSamples();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoreSample> getCoreSampleById(@PathVariable Long id) {
        return coreSampleService.getCoreSampleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CoreSample createCoreSample(@RequestBody CoreSample coreSample) {
        return coreSampleService.saveCoreSample(coreSample);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoreSample> updateCoreSample(@PathVariable Long id, @RequestBody CoreSample coreSampleDetails) {
        try {
            CoreSample updatedCoreSample = coreSampleService.updateCoreSample(id, coreSampleDetails);
            return ResponseEntity.ok(updatedCoreSample);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoreSample(@PathVariable Long id) {
        coreSampleService.deleteCoreSample(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/basin")
    public List<CoreSample> searchByBasinName(@RequestParam String basinName) {
        return coreSampleService.searchByBasinName(basinName);
    }

    @GetMapping("/search/lithology")
    public List<CoreSample> searchByLithology(@RequestParam String lithology) {
        return coreSampleService.searchByLithology(lithology);
    }

    @GetMapping("/search/depth")
    public List<CoreSample> searchByDepthRange(@RequestParam Double minDepth, @RequestParam Double maxDepth) {
        return coreSampleService.searchByDepthRange(minDepth, maxDepth);
    }
}
