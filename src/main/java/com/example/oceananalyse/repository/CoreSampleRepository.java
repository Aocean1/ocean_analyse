package com.example.oceananalyse.repository;

import com.example.oceananalyse.entity.CoreSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoreSampleRepository extends JpaRepository<CoreSample, Long> {
    
    Optional<CoreSample> findBySampleNo(String sampleNo);
    
    List<CoreSample> findByBasinName(String basinName);
    
    List<CoreSample> findByLithology(String lithology);
    
    List<CoreSample> findByDepthFromBetween(Double minDepth, Double maxDepth);
    
    List<CoreSample> findByFormation(String formation);
    
    List<CoreSample> findAllByOrderByCreatedAtDesc();
}
