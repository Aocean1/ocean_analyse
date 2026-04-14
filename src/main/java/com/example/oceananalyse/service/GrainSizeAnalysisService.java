package com.example.oceananalyse.service;

import com.example.oceananalyse.entity.CoreSample;
import com.example.oceananalyse.entity.GrainSizeAnalysis;
import com.example.oceananalyse.repository.CoreSampleRepository;
import com.example.oceananalyse.repository.GrainSizeAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GrainSizeAnalysisService {

    @Autowired
    private GrainSizeAnalysisRepository grainSizeAnalysisRepository;

    @Autowired
    private CoreSampleRepository coreSampleRepository;

    public GrainSizeAnalysis saveGrainSizeAnalysis(Long coreSampleId, GrainSizeAnalysis grainSizeAnalysis) {
        CoreSample coreSample = coreSampleRepository.findById(coreSampleId)
                .orElseThrow(() -> new RuntimeException("岩心样本不存在，ID: " + coreSampleId));
        
        grainSizeAnalysis.setCoreSample(coreSample);
        return grainSizeAnalysisRepository.save(grainSizeAnalysis);
    }

    public Optional<GrainSizeAnalysis> getGrainSizeAnalysisByCoreSampleId(Long coreSampleId) {
        return grainSizeAnalysisRepository.findByCoreSampleId(coreSampleId);
    }

    public GrainSizeAnalysis updateGrainSizeAnalysis(Long id, GrainSizeAnalysis grainSizeAnalysisDetails) {
        GrainSizeAnalysis grainSizeAnalysis = grainSizeAnalysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("粒度分析不存在，ID: " + id));
        
        grainSizeAnalysis.setAvgGrainSize(grainSizeAnalysisDetails.getAvgGrainSize());
        grainSizeAnalysis.setMedianSize(grainSizeAnalysisDetails.getMedianSize());
        grainSizeAnalysis.setSortingCoefficient(grainSizeAnalysisDetails.getSortingCoefficient());
        grainSizeAnalysis.setSkewness(grainSizeAnalysisDetails.getSkewness());
        grainSizeAnalysis.setKurtosis(grainSizeAnalysisDetails.getKurtosis());
        grainSizeAnalysis.setGrainType(grainSizeAnalysisDetails.getGrainType());
        grainSizeAnalysis.setRoundness(grainSizeAnalysisDetails.getRoundness());
        grainSizeAnalysis.setSortingDegree(grainSizeAnalysisDetails.getSortingDegree());
        grainSizeAnalysis.setAnalysisMethod(grainSizeAnalysisDetails.getAnalysisMethod());
        grainSizeAnalysis.setAnalysisNote(grainSizeAnalysisDetails.getAnalysisNote());
        
        return grainSizeAnalysisRepository.save(grainSizeAnalysis);
    }

    public void deleteGrainSizeAnalysis(Long id) {
        grainSizeAnalysisRepository.deleteById(id);
    }
}
