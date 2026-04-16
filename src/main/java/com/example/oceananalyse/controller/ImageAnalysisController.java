package com.example.oceananalyse.controller;

import com.example.oceananalyse.entity.CoreSample;
import com.example.oceananalyse.entity.CoreImage;
import com.example.oceananalyse.repository.CoreImageRepository;
import com.example.oceananalyse.repository.CoreSampleRepository;
import com.example.oceananalyse.service.ImageAnalysisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/image-analysis")
public class ImageAnalysisController {

    private final ImageAnalysisService imageAnalysisService;
    private final CoreImageRepository coreImageRepository;
    private final CoreSampleRepository coreSampleRepository;

    public ImageAnalysisController(ImageAnalysisService imageAnalysisService,
                                   CoreImageRepository coreImageRepository,
                                   CoreSampleRepository coreSampleRepository) {
        this.imageAnalysisService = imageAnalysisService;
        this.coreImageRepository = coreImageRepository;
        this.coreSampleRepository = coreSampleRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadAndAnalyze(@RequestParam("file") MultipartFile file,
                                              @RequestParam(value = "coreId", required = false) Long coreId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (file == null || file.isEmpty()) {
                result.put("success", false);
                result.put("message", "上传文件不能为空");
                return ResponseEntity.badRequest().body(result);
            }
            
            String filename = file.getOriginalFilename();
            if (filename == null || (!filename.endsWith(".jpg") && !filename.endsWith(".jpeg") && 
                                   !filename.endsWith(".png") && !filename.endsWith(".gif"))) {
                result.put("success", false);
                result.put("message", "只支持 JPG、JPEG、PNG、GIF 格式的图片");
                return ResponseEntity.badRequest().body(result);
            }
            
            Map<String, Object> analysis = imageAnalysisService.analyzeImage(file);
            String imageUrl = (String) analysis.get("imageUrl");
            
            CoreImage coreImage = new CoreImage();
            coreImage.setImageUrl(imageUrl);
            coreImage.setWidth((Integer) analysis.get("width"));
            coreImage.setHeight((Integer) analysis.get("height"));
            coreImage.setFileSize((Long) analysis.get("fileSize"));
            coreImage.setFormat((String) analysis.get("format"));
            
            if (coreId != null) {
                Optional<CoreSample> coreSample = coreSampleRepository.findById(coreId);
                coreSample.ifPresent(coreImage::setCoreSample);
            }
            
            coreImage = coreImageRepository.save(coreImage);
            
            result.put("success", true);
            result.put("message", "图片上传并分析成功");
            result.put("imageId", coreImage.getId());
            result.put("analysis", analysis);
            
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", "参数错误: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "图片处理失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "服务器内部错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @GetMapping("/core/{coreId}")
    public ResponseEntity<List<CoreImage>> getImagesByCore(@PathVariable Long coreId) {
        List<CoreImage> images = coreImageRepository.findByCoreSampleId(coreId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImageAnalysis(@PathVariable Long id) {
        Optional<CoreImage> image = coreImageRepository.findById(id);
        if (image.isPresent()) {
            return ResponseEntity.ok(image.get());
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "图片不存在");
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteImage(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            coreImageRepository.deleteById(id);
            result.put("success", true);
            result.put("message", "删除成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
}
