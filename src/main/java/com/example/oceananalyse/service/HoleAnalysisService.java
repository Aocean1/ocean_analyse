package com.example.oceananalyse.service;

import com.example.oceananalyse.entity.CoreSample;
import com.example.oceananalyse.entity.HoleAnalysis;
import com.example.oceananalyse.repository.CoreSampleRepository;
import com.example.oceananalyse.repository.HoleAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HoleAnalysisService {

    @Autowired
    private HoleAnalysisRepository holeAnalysisRepository;

    @Autowired
    private CoreSampleRepository coreSampleRepository;

    public HoleAnalysis saveHoleAnalysis(Long coreSampleId, HoleAnalysis holeAnalysis) {
        CoreSample coreSample = coreSampleRepository.findById(coreSampleId)
                .orElseThrow(() -> new RuntimeException("岩心样本不存在，ID: " + coreSampleId));
        
        holeAnalysis.setCoreSample(coreSample);
        return holeAnalysisRepository.save(holeAnalysis);
    }

    public Optional<HoleAnalysis> getHoleAnalysisByCoreSampleId(Long coreSampleId) {
        return holeAnalysisRepository.findByCoreSampleId(coreSampleId);
    }

    public HoleAnalysis updateHoleAnalysis(Long id, HoleAnalysis holeAnalysisDetails) {
        HoleAnalysis holeAnalysis = holeAnalysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("孔洞分析不存在，ID: " + id));
        
        holeAnalysis.setHoleDensity(holeAnalysisDetails.getHoleDensity());
        holeAnalysis.setAvgDiameter(holeAnalysisDetails.getAvgDiameter());
        holeAnalysis.setMaxDiameter(holeAnalysisDetails.getMaxDiameter());
        holeAnalysis.setMinDiameter(holeAnalysisDetails.getMinDiameter());
        holeAnalysis.setHoleShape(holeAnalysisDetails.getHoleShape());
        holeAnalysis.setHoleType(holeAnalysisDetails.getHoleType());
        holeAnalysis.setPorosity(holeAnalysisDetails.getPorosity());
        holeAnalysis.setAnalysisMethod(holeAnalysisDetails.getAnalysisMethod());
        holeAnalysis.setAnalysisNote(holeAnalysisDetails.getAnalysisNote());
        
        return holeAnalysisRepository.save(holeAnalysis);
    }

    public void deleteHoleAnalysis(Long id) {
        holeAnalysisRepository.deleteById(id);
    }
}
