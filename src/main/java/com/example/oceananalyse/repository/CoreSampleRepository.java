package com.example.oceananalyse.repository;

import com.example.oceananalyse.entity.CoreSample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    @Query("SELECT c FROM CoreSample c WHERE " +
           "(:sampleNo IS NULL OR c.sampleNo LIKE %:sampleNo%) AND " +
           "(:basinName IS NULL OR c.basinName LIKE %:basinName%) AND " +
           "(:lithology IS NULL OR c.lithology LIKE %:lithology%) AND " +
           "(:minDepth IS NULL OR c.depthFrom >= :minDepth) AND " +
           "(:maxDepth IS NULL OR c.depthTo <= :maxDepth)")
    List<CoreSample> searchByMultipleConditions(
        @Param("sampleNo") String sampleNo,
        @Param("basinName") String basinName,
        @Param("lithology") String lithology,
        @Param("minDepth") Double minDepth,
        @Param("maxDepth") Double maxDepth
    );
}
