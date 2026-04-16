package com.example.oceananalyse.repository;

import com.example.oceananalyse.entity.CoreImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoreImageRepository extends JpaRepository<CoreImage, Long> {
    List<CoreImage> findByCoreSampleId(Long coreId);
}