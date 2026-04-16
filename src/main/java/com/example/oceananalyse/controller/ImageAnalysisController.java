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
import java.util.*;
import java.util.stream.Collectors;

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
            
            coreImage.setHoleCount((Integer) analysis.get("holeCount"));
            coreImage.setAvgHoleSize((Double) analysis.get("avgHoleSize"));
            coreImage.setPorosity((Double) analysis.get("porosity"));
            coreImage.setGrainSize((Double) analysis.get("grainSize"));
            coreImage.setGrainCount((Integer) analysis.get("grainCount"));
            coreImage.setRoundness((Double) analysis.get("roundness"));
            coreImage.setGrainShape((String) analysis.get("grainShape"));
            coreImage.setSorting((String) analysis.get("sorting"));
            coreImage.setFractureCount((Integer) analysis.get("fractureCount"));
            coreImage.setFractureLength((Double) analysis.get("fractureLength"));
            coreImage.setFractureOrientation((String) analysis.get("fractureOrientation"));
            coreImage.setBrightness((Double) analysis.get("brightness"));
            coreImage.setContrast((Double) analysis.get("contrast"));
            coreImage.setEntropy((Double) analysis.get("entropy"));
            
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
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<CoreImage> imageOpt = coreImageRepository.findById(id);
            if (imageOpt.isPresent()) {
                CoreImage image = imageOpt.get();
                
                Map<String, Object> analysis = new HashMap<>();
                analysis.put("imageUrl", image.getImageUrl());
                analysis.put("width", image.getWidth());
                analysis.put("height", image.getHeight());
                analysis.put("fileSize", image.getFileSize());
                analysis.put("format", image.getFormat());
                analysis.put("uploadTime", image.getUploadTime());
                
                analysis.put("holeCount", image.getHoleCount() != null ? image.getHoleCount() : 0);
                analysis.put("avgHoleSize", image.getAvgHoleSize() != null ? image.getAvgHoleSize() : 0.0);
                analysis.put("porosity", image.getPorosity() != null ? image.getPorosity() : 0.0);
                analysis.put("grainSize", image.getGrainSize() != null ? image.getGrainSize() : 0.0);
                analysis.put("grainCount", image.getGrainCount() != null ? image.getGrainCount() : 0);
                analysis.put("roundness", image.getRoundness() != null ? image.getRoundness() : 0.0);
                analysis.put("grainShape", image.getGrainShape() != null ? image.getGrainShape() : "-");
                analysis.put("sorting", image.getSorting() != null ? image.getSorting() : "-");
                analysis.put("fractureCount", image.getFractureCount() != null ? image.getFractureCount() : 0);
                analysis.put("fractureLength", image.getFractureLength() != null ? image.getFractureLength() : 0.0);
                analysis.put("fractureOrientation", image.getFractureOrientation() != null ? image.getFractureOrientation() : "无");
                analysis.put("brightness", image.getBrightness() != null ? image.getBrightness() : 0.0);
                analysis.put("contrast", image.getContrast() != null ? image.getContrast() : 0.0);
                analysis.put("entropy", image.getEntropy() != null ? image.getEntropy() : 0.0);
                
                result.put("success", true);
                result.put("analysis", analysis);
                result.put("message", "获取分析结果成功");
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "图片不存在");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取分析结果失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/reanalyze/{id}")
    public ResponseEntity<?> reanalyzeImage(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            System.out.println("=== 开始重新分析图片，ID: " + id + " ===");
            
            if (id == null || id <= 0) {
                String errorMsg = "无效的图片ID: " + id;
                result.put("success", false);
                result.put("message", errorMsg);
                result.put("errorCode", "INVALID_ID");
                return ResponseEntity.badRequest().body(result);
            }
            
            Optional<CoreImage> imageOpt = coreImageRepository.findById(id);
            if (imageOpt.isPresent()) {
                CoreImage coreImage = imageOpt.get();
                
                Map<String, Object> oldAnalysis = collectCurrentAnalysis(coreImage);
                
                String imageUrl = coreImage.getImageUrl();
                if (imageUrl == null || imageUrl.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "图片URL为空");
                    result.put("errorCode", "EMPTY_IMAGE_URL");
                    return ResponseEntity.badRequest().body(result);
                }
                
                if (!imageUrl.startsWith("/uploads/")) {
                    result.put("success", false);
                    result.put("message", "无效的图片路径格式，路径必须以 /uploads/ 开头");
                    result.put("errorCode", "INVALID_PATH_FORMAT");
                    return ResponseEntity.badRequest().body(result);
                }
                
                String relativePath = imageUrl.replace("/uploads/", "");
                java.io.File uploadDir = new java.io.File("uploads");
                java.io.File file = new java.io.File(uploadDir, relativePath);
                
                if (!file.exists()) {
                    result.put("success", false);
                    result.put("message", "图片文件不存在: " + file.getAbsolutePath());
                    result.put("errorCode", "FILE_NOT_FOUND");
                    return ResponseEntity.badRequest().body(result);
                }
                
                if (!file.canRead()) {
                    result.put("success", false);
                    result.put("message", "图片文件不可读，请检查文件权限");
                    result.put("errorCode", "FILE_NOT_READABLE");
                    return ResponseEntity.badRequest().body(result);
                }
                
                if (file.isDirectory()) {
                    result.put("success", false);
                    result.put("message", "指定的路径是目录，不是文件");
                    result.put("errorCode", "IS_DIRECTORY");
                    return ResponseEntity.badRequest().body(result);
                }
                
                if (file.length() == 0) {
                    result.put("success", false);
                    result.put("message", "图片文件为空");
                    result.put("errorCode", "EMPTY_FILE");
                    return ResponseEntity.badRequest().body(result);
                }
                
                String contentType = getContentType(file.getName());
                
                Map<String, Object> newAnalysis = imageAnalysisService.reanalyzeFromFile(file, imageUrl, contentType);
                
                Map<String, Object> comparison = imageAnalysisService.compareAnalysisResults(oldAnalysis, newAnalysis);
                
                updateCoreImageFromAnalysis(coreImage, newAnalysis);
                coreImageRepository.save(coreImage);
                
                result.put("success", true);
                result.put("message", "重新分析成功");
                result.put("analysis", newAnalysis);
                result.put("comparison", comparison);
                Map<String, Object> debugInfo = new HashMap<>();
                debugInfo.put("imageId", id);
                debugInfo.put("imageUrl", imageUrl);
                debugInfo.put("filePath", file.getAbsolutePath());
                debugInfo.put("fileSize", file.length() + " bytes");
                result.put("debugInfo", debugInfo);
                
                return ResponseEntity.ok(result);
            } else {
                result.put("success", false);
                result.put("message", "图片不存在，ID: " + id);
                result.put("errorCode", "IMAGE_NOT_FOUND");
                return ResponseEntity.notFound().build();
            }
            
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "文件操作失败: " + e.getMessage());
            result.put("errorCode", "IO_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        } catch (OutOfMemoryError e) {
            result.put("success", false);
            result.put("message", "图片分析时内存不足，请尝试使用更小的图片");
            result.put("errorCode", "OUT_OF_MEMORY");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "重新分析失败: " + e.getMessage());
            result.put("errorCode", "UNKNOWN_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/reanalyze/batch")
    public ResponseEntity<?> batchReanalyzeImages(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> reanalyzeResults = new ArrayList<>();
        
        try {
            @SuppressWarnings("unchecked")
            List<Long> imageIds = ((List<Number>) request.get("imageIds")).stream()
                .map(Number::longValue)
                .collect(Collectors.toList());
            
            if (imageIds == null || imageIds.isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择要重新分析的图片");
                return ResponseEntity.badRequest().body(result);
            }
            
            int successCount = 0;
            int failCount = 0;
            
            for (Long imageId : imageIds) {
                Map<String, Object> itemResult = new HashMap<>();
                itemResult.put("imageId", imageId);
                
                try {
                    Optional<CoreImage> imageOpt = coreImageRepository.findById(imageId);
                    if (!imageOpt.isPresent()) {
                        itemResult.put("success", false);
                        itemResult.put("message", "图片不存在");
                        failCount++;
                        reanalyzeResults.add(itemResult);
                        continue;
                    }
                    
                    CoreImage coreImage = imageOpt.get();
                    String imageUrl = coreImage.getImageUrl();
                    
                    if (imageUrl == null || !imageUrl.startsWith("/uploads/")) {
                        itemResult.put("success", false);
                        itemResult.put("message", "无效的图片路径");
                        failCount++;
                        reanalyzeResults.add(itemResult);
                        continue;
                    }
                    
                    String relativePath = imageUrl.replace("/uploads/", "");
                    java.io.File file = new java.io.File("uploads", relativePath);
                    
                    if (!file.exists() || !file.canRead()) {
                        itemResult.put("success", false);
                        itemResult.put("message", "图片文件不存在或不可读");
                        failCount++;
                        reanalyzeResults.add(itemResult);
                        continue;
                    }
                    
                    String contentType = getContentType(file.getName());
                    Map<String, Object> newAnalysis = imageAnalysisService.reanalyzeFromFile(file, imageUrl, contentType);
                    
                    updateCoreImageFromAnalysis(coreImage, newAnalysis);
                    coreImageRepository.save(coreImage);
                    
                    itemResult.put("success", true);
                    itemResult.put("message", "重新分析成功");
                    itemResult.put("analysis", newAnalysis);
                    successCount++;
                    
                } catch (Exception e) {
                    itemResult.put("success", false);
                    itemResult.put("message", "分析失败: " + e.getMessage());
                    failCount++;
                }
                
                reanalyzeResults.add(itemResult);
            }
            
            result.put("success", true);
            result.put("message", String.format("批量重新分析完成，成功: %d，失败: %d", successCount, failCount));
            result.put("totalCount", imageIds.size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("results", reanalyzeResults);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "批量重新分析失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    @PostMapping("/reanalyze/core/{coreId}")
    public ResponseEntity<?> reanalyzeAllImagesForCore(@PathVariable Long coreId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Optional<CoreSample> coreSampleOpt = coreSampleRepository.findById(coreId);
            if (!coreSampleOpt.isPresent()) {
                result.put("success", false);
                result.put("message", "岩心样本不存在");
                return ResponseEntity.badRequest().body(result);
            }
            
            List<CoreImage> images = coreImageRepository.findByCoreSampleId(coreId);
            if (images.isEmpty()) {
                result.put("success", false);
                result.put("message", "该岩心没有关联的图片");
                return ResponseEntity.badRequest().body(result);
            }
            
            List<Long> imageIds = images.stream()
                .map(CoreImage::getId)
                .collect(Collectors.toList());
            
            Map<String, Object> request = new HashMap<>();
            request.put("imageIds", imageIds);
            
            ResponseEntity<?> batchResult = batchReanalyzeImages(request);
            return batchResult;
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "重新分析岩心所有图片失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    private Map<String, Object> collectCurrentAnalysis(CoreImage coreImage) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("holeCount", coreImage.getHoleCount() != null ? coreImage.getHoleCount() : 0);
        analysis.put("avgHoleSize", coreImage.getAvgHoleSize() != null ? coreImage.getAvgHoleSize() : 0.0);
        analysis.put("porosity", coreImage.getPorosity() != null ? coreImage.getPorosity() : 0.0);
        analysis.put("grainSize", coreImage.getGrainSize() != null ? coreImage.getGrainSize() : 0.0);
        analysis.put("grainCount", coreImage.getGrainCount() != null ? coreImage.getGrainCount() : 0);
        analysis.put("roundness", coreImage.getRoundness() != null ? coreImage.getRoundness() : 0.0);
        analysis.put("grainShape", coreImage.getGrainShape() != null ? coreImage.getGrainShape() : "-");
        analysis.put("sorting", coreImage.getSorting() != null ? coreImage.getSorting() : "-");
        analysis.put("fractureCount", coreImage.getFractureCount() != null ? coreImage.getFractureCount() : 0);
        analysis.put("fractureLength", coreImage.getFractureLength() != null ? coreImage.getFractureLength() : 0.0);
        analysis.put("fractureOrientation", coreImage.getFractureOrientation() != null ? coreImage.getFractureOrientation() : "无");
        analysis.put("brightness", coreImage.getBrightness() != null ? coreImage.getBrightness() : 0.0);
        analysis.put("contrast", coreImage.getContrast() != null ? coreImage.getContrast() : 0.0);
        analysis.put("entropy", coreImage.getEntropy() != null ? coreImage.getEntropy() : 0.0);
        return analysis;
    }

    private void updateCoreImageFromAnalysis(CoreImage coreImage, Map<String, Object> analysis) {
        coreImage.setHoleCount((Integer) analysis.get("holeCount"));
        coreImage.setAvgHoleSize((Double) analysis.get("avgHoleSize"));
        coreImage.setPorosity((Double) analysis.get("porosity"));
        coreImage.setGrainSize((Double) analysis.get("grainSize"));
        coreImage.setGrainCount((Integer) analysis.get("grainCount"));
        coreImage.setRoundness((Double) analysis.get("roundness"));
        coreImage.setGrainShape((String) analysis.get("grainShape"));
        coreImage.setSorting((String) analysis.get("sorting"));
        coreImage.setFractureCount((Integer) analysis.get("fractureCount"));
        coreImage.setFractureLength((Double) analysis.get("fractureLength"));
        coreImage.setFractureOrientation((String) analysis.get("fractureOrientation"));
        coreImage.setBrightness((Double) analysis.get("brightness"));
        coreImage.setContrast((Double) analysis.get("contrast"));
        coreImage.setEntropy((Double) analysis.get("entropy"));
    }

    private String getContentType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        }
        return "image/jpeg";
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
