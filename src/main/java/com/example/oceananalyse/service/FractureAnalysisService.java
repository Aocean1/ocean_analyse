package com.example.oceananalyse.service;

import com.example.oceananalyse.entity.CoreSample;
import com.example.oceananalyse.entity.FractureAnalysis;
import com.example.oceananalyse.repository.CoreSampleRepository;
import com.example.oceananalyse.repository.FractureAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FractureAnalysisService {

    @Autowired
    private FractureAnalysisRepository fractureAnalysisRepository;

    @Autowired
    private CoreSampleRepository coreSampleRepository;

    public FractureAnalysis saveFractureAnalysis(Long coreSampleId, FractureAnalysis fractureAnalysis) {
        CoreSample coreSample = coreSampleRepository.findById(coreSampleId)
                .orElseThrow(() -> new RuntimeException("岩心样本不存在，ID: " + coreSampleId));
        
        fractureAnalysis.setCoreSample(coreSample);
        return fractureAnalysisRepository.save(fractureAnalysis);
    }

    public Optional<FractureAnalysis> getFractureAnalysisByCoreSampleId(Long coreSampleId) {
        return fractureAnalysisRepository.findByCoreSampleId(coreSampleId);
    }

    public FractureAnalysis updateFractureAnalysis(Long id, FractureAnalysis fractureAnalysisDetails) {
        FractureAnalysis fractureAnalysis = fractureAnalysisRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("裂缝分析不存在，ID: " + id));
        
        fractureAnalysis.setFractureDensity(fractureAnalysisDetails.getFractureDensity());
        fractureAnalysis.setAvgLength(fractureAnalysisDetails.getAvgLength());
        fractureAnalysis.setAvgWidth(fractureAnalysisDetails.getAvgWidth());
        fractureAnalysis.setFractureOrientation(fractureAnalysisDetails.getFractureOrientation());
        fractureAnalysis.setFractureType(fractureAnalysisDetails.getFractureType());
        fractureAnalysis.setFillingStatus(fractureAnalysisDetails.getFillingStatus());
        fractureAnalysis.setConnectivity(fractureAnalysisDetails.getConnectivity());
        fractureAnalysis.setAnalysisMethod(fractureAnalysisDetails.getAnalysisMethod());
        fractureAnalysis.setAnalysisNote(fractureAnalysisDetails.getAnalysisNote());
        
        return fractureAnalysisRepository.save(fractureAnalysis);
    }

    public void deleteFractureAnalysis(Long id) {
        fractureAnalysisRepository.deleteById(id);
    }
}
