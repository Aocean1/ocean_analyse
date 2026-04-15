package com.example.oceananalyse.service;

import com.example.oceananalyse.entity.CoreSample;
import com.example.oceananalyse.repository.CoreSampleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class CoreSampleService {

    @Autowired
    private CoreSampleRepository coreSampleRepository;

    public List<CoreSample> getAllCoreSamples() {
        return coreSampleRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<CoreSample> getCoreSampleById(Long id) {
        return coreSampleRepository.findById(id);
    }

    public Optional<CoreSample> getCoreSampleBySampleNo(String sampleNo) {
        return coreSampleRepository.findBySampleNo(sampleNo);
    }

    public CoreSample saveCoreSample(CoreSample coreSample) {
        return coreSampleRepository.save(coreSample);
    }

    public CoreSample updateCoreSample(Long id, CoreSample coreSampleDetails) {
        CoreSample coreSample = coreSampleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("岩心样本不存在，ID: " + id));
        
        coreSample.setSampleNo(coreSampleDetails.getSampleNo());
        coreSample.setBasinName(coreSampleDetails.getBasinName());
        coreSample.setLocation(coreSampleDetails.getLocation());
        coreSample.setDepthFrom(coreSampleDetails.getDepthFrom());
        coreSample.setDepthTo(coreSampleDetails.getDepthTo());
        coreSample.setFormation(coreSampleDetails.getFormation());
        coreSample.setLithology(coreSampleDetails.getLithology());
        coreSample.setColor(coreSampleDetails.getColor());
        coreSample.setDescription(coreSampleDetails.getDescription());
        coreSample.setImagePath(coreSampleDetails.getImagePath());
        
        return coreSampleRepository.save(coreSample);
    }

    public void deleteCoreSample(Long id) {
        coreSampleRepository.deleteById(id);
    }

    public List<CoreSample> searchByBasinName(String basinName) {
        return coreSampleRepository.findByBasinName(basinName);
    }

    public List<CoreSample> searchByLithology(String lithology) {
        return coreSampleRepository.findByLithology(lithology);
    }

    public List<CoreSample> searchByDepthRange(Double minDepth, Double maxDepth) {
        return coreSampleRepository.findByDepthFromBetween(minDepth, maxDepth);
    }
    
    public List<CoreSample> searchByConditions(String sampleNo, String basinName, 
                                                String lithology, Double minDepth, Double maxDepth) {
        sampleNo = StringUtils.isEmpty(sampleNo) ? null : sampleNo.trim();
        basinName = StringUtils.isEmpty(basinName) ? null : basinName.trim();
        lithology = StringUtils.isEmpty(lithology) ? null : lithology.trim();
        
        return coreSampleRepository.searchByMultipleConditions(sampleNo, basinName, lithology, minDepth, maxDepth);
    }
}
