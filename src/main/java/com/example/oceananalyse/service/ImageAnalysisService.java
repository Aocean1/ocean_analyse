package com.example.oceananalyse.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ImageAnalysisService {

    private final FileStorageService fileStorageService;

    public ImageAnalysisService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    public Map<String, Object> analyzeImage(MultipartFile file) throws IOException {
        String imageUrl = fileStorageService.uploadFile(file);
        return analyzeImageInternal(file.getBytes(), file.getSize(), file.getContentType(), imageUrl);
    }

    public Map<String, Object> analyzeImageWithoutUpload(MultipartFile file, String existingImageUrl) throws IOException {
        return analyzeImageInternal(file.getBytes(), file.getSize(), file.getContentType(), existingImageUrl);
    }

    public Map<String, Object> reanalyzeFromFile(java.io.File file, String existingImageUrl, String contentType) throws IOException {
        byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());
        return analyzeImageInternal(fileBytes, file.length(), contentType, existingImageUrl);
    }

    public List<Map<String, Object>> batchReanalyze(List<java.io.File> files, List<String> imageUrls, List<String> contentTypes) throws IOException {
        List<Map<String, Object>> results = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            try {
                Map<String, Object> result = reanalyzeFromFile(files.get(i), 
                    imageUrls.get(i), contentTypes.get(i));
                result.put("success", true);
                result.put("index", i);
                results.add(result);
            } catch (Exception e) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("success", false);
                errorResult.put("index", i);
                errorResult.put("error", e.getMessage());
                results.add(errorResult);
            }
        }
        return results;
    }

    private Map<String, Object> analyzeImageInternal(byte[] fileBytes, long fileSize, String contentType, String imageUrl) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
        int width = image.getWidth();
        int height = image.getHeight();
        
        Map<String, Object> analysisResult = new HashMap<>();
        analysisResult.put("imageUrl", imageUrl);
        analysisResult.put("width", width);
        analysisResult.put("height", height);
        analysisResult.put("fileSize", fileSize);
        analysisResult.put("format", contentType);
        
        BufferedImage grayImage = convertToGrayscale(image);
        analysisResult.putAll(analyzeImageFeatures(grayImage));
        analysisResult.putAll(detectHoles(grayImage));
        analysisResult.putAll(analyzeGrainSize(grayImage));
        analysisResult.putAll(detectFractures(grayImage));
        
        analysisResult.put("analysisTime", new Date().toString());
        
        return analysisResult;
    }

    public Map<String, Object> compareAnalysisResults(Map<String, Object> oldResult, Map<String, Object> newResult) {
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("hasChanges", false);
        
        List<String> numericFields = Arrays.asList("holeCount", "avgHoleSize", "porosity", 
            "grainSize", "grainCount", "roundness", "fractureCount", 
            "fractureLength", "brightness", "contrast", "entropy");
        List<String> stringFields = Arrays.asList("grainShape", "sorting", "fractureOrientation");
        
        Map<String, Object> changes = new HashMap<>();
        
        for (String field : numericFields) {
            Object oldVal = oldResult.get(field);
            Object newVal = newResult.get(field);
            
            if (oldVal instanceof Number && newVal instanceof Number) {
                double oldNum = ((Number) oldVal).doubleValue();
                double newNum = ((Number) newVal).doubleValue();
                double diff = Math.abs(newNum - oldNum);
                
                if (diff > 0.001) {
                    comparison.put("hasChanges", true);
                    Map<String, Object> changeDetail = new HashMap<>();
                    changeDetail.put("oldValue", oldNum);
                    changeDetail.put("newValue", newNum);
                    changeDetail.put("difference", String.format("%.4f", diff));
                    changeDetail.put("percentChange", String.format("%.2f%%", (newNum - oldNum) / (oldNum == 0 ? 1 : oldNum) * 100));
                    changes.put(field, changeDetail);
                }
            }
        }
        
        for (String field : stringFields) {
            String oldVal = (String) oldResult.get(field);
            String newVal = (String) newResult.get(field);
            
            if (!Objects.equals(oldVal, newVal)) {
                comparison.put("hasChanges", true);
                Map<String, Object> changeDetail = new HashMap<>();
                changeDetail.put("oldValue", oldVal);
                changeDetail.put("newValue", newVal);
                changes.put(field, changeDetail);
            }
        }
        
        comparison.put("changes", changes);
        comparison.put("changeCount", changes.size());
        
        return comparison;
    }

    private BufferedImage convertToGrayscale(BufferedImage image) {
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int gray = (int) (0.299 * ((rgb >> 16) & 0xFF) + 
                                  0.587 * ((rgb >> 8) & 0xFF) + 
                                  0.114 * (rgb & 0xFF));
                grayImage.setRGB(x, y, (gray << 16) | (gray << 8) | gray);
            }
        }
        return grayImage;
    }

    private Map<String, Object> analyzeImageFeatures(BufferedImage grayImage) {
        Map<String, Object> features = new HashMap<>();
        
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        int totalPixels = width * height;
        
        if (totalPixels == 0) {
            features.put("brightness", 0.0);
            features.put("contrast", 0.0);
            features.put("entropy", 0.0);
            return features;
        }
        
        int[] histogram = new int[256];
        long sum = 0;
        int minGray = 255;
        int maxGray = 0;
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = grayImage.getRGB(x, y) & 0xFF;
                histogram[gray]++;
                sum += gray;
                minGray = Math.min(minGray, gray);
                maxGray = Math.max(maxGray, gray);
            }
        }
        
        double meanBrightness = (double) sum / totalPixels;
        features.put("brightness", Math.round(meanBrightness / 255.0 * 100) / 100.0);
        
        double dynamicRange = maxGray - minGray;
        features.put("contrast", Math.round(dynamicRange / 255.0 * 100) / 100.0);
        
        double entropy = 0;
        for (int count : histogram) {
            if (count > 0) {
                double prob = (double) count / totalPixels;
                entropy -= prob * Math.log(prob) / Math.log(2);
            }
        }
        features.put("entropy", Math.round(entropy * 100) / 100.0);
        
        return features;
    }

    private Map<String, Object> detectHoles(BufferedImage grayImage) {
        Map<String, Object> result = new HashMap<>();
        
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        
        BufferedImage blurred = applyGaussianBlur(grayImage, 5);
        
        int threshold = calculateOtsuThreshold(blurred);
        int adaptiveThreshold = Math.min(threshold + 30, 180);
        
        boolean[][] binaryImage = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = blurred.getRGB(x, y) & 0xFF;
                binaryImage[y][x] = gray < adaptiveThreshold;
            }
        }
        
        boolean[][] inverted = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                inverted[y][x] = !binaryImage[y][x];
            }
        }
        
        List<Blob> holes = findBlobs(inverted, width, height);
        holes.removeIf(blob -> blob.area < 5 || blob.area > 20000);
        
        result.put("holeCount", holes.size());
        
        if (!holes.isEmpty()) {
            double avgArea = holes.stream().mapToInt(b -> b.area).average().orElse(0);
            double maxArea = holes.stream().mapToInt(b -> b.area).max().orElse(0);
            double minArea = holes.stream().mapToInt(b -> b.area).min().orElse(0);
            
            double avgDiameter = Math.sqrt(4 * avgArea / Math.PI);
            double maxDiameter = Math.sqrt(4 * maxArea / Math.PI);
            double minDiameter = Math.sqrt(4 * minArea / Math.PI);
            
            double scaleFactor = 0.1;
            result.put("avgHoleSize", Math.round(avgDiameter * scaleFactor * 100) / 100.0);
            result.put("maxHoleSize", Math.round(maxDiameter * scaleFactor * 100) / 100.0);
            result.put("minHoleSize", Math.round(minDiameter * scaleFactor * 100) / 100.0);
            
            int holePixels = holes.stream().mapToInt(b -> b.area).sum();
            double porosity = (double) holePixels / (width * height) * 100;
            result.put("porosity", Math.round(porosity * 100) / 100.0);
        } else {
            result.put("avgHoleSize", 0.0);
            result.put("maxHoleSize", 0.0);
            result.put("minHoleSize", 0.0);
            result.put("porosity", 0.0);
        }
        
        return result;
    }

    private BufferedImage applyGaussianBlur(BufferedImage image, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        
        double[] kernel = createGaussianKernel(kernelSize);
        int half = kernelSize / 2;
        
        for (int y = half; y < height - half; y++) {
            for (int x = half; x < width - half; x++) {
                double sum = 0;
                for (int ky = -half; ky <= half; ky++) {
                    for (int kx = -half; kx <= half; kx++) {
                        int gray = image.getRGB(x + kx, y + ky) & 0xFF;
                        sum += gray * kernel[ky + half + (kx + half) * kernelSize];
                    }
                }
                result.setRGB(x, y, (int) sum << 16 | (int) sum << 8 | (int) sum);
            }
        }
        
        return result;
    }

    private double[] createGaussianKernel(int size) {
        double[] kernel = new double[size * size];
        double sigma = size / 3.0;
        double sum = 0;
        int half = size / 2;
        
        for (int y = -half; y <= half; y++) {
            for (int x = -half; x <= half; x++) {
                double value = Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
                kernel[y + half + (x + half) * size] = value;
                sum += value;
            }
        }
        
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }
        
        return kernel;
    }

    private int calculateOtsuThreshold(BufferedImage image) {
        int[] histogram = new int[256];
        int total = 0;
        
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int gray = image.getRGB(x, y) & 0xFF;
                histogram[gray]++;
                total++;
            }
        }
        
        double sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }
        
        double sumB = 0;
        int wB = 0;
        int wF = 0;
        double maxVariance = 0;
        int threshold = 0;
        
        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0) continue;
            wF = total - wB;
            if (wF == 0) break;
            
            sumB += (double) i * histogram[i];
            double mB = sumB / wB;
            double mF = (sum - sumB) / wF;
            
            double variance = (double) wB * wF * (mB - mF) * (mB - mF);
            
            if (variance > maxVariance) {
                maxVariance = variance;
                threshold = i;
            }
        }
        
        return threshold;
    }

    private List<Blob> findBlobs(boolean[][] binaryImage, int width, int height) {
        List<Blob> blobs = new ArrayList<>();
        boolean[][] visited = new boolean[height][width];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (binaryImage[y][x] && !visited[y][x]) {
                    Blob blob = new Blob();
                    floodFill(binaryImage, visited, x, y, width, height, blob);
                    blobs.add(blob);
                }
            }
        }
        
        return blobs;
    }

    private void floodFill(boolean[][] binaryImage, boolean[][] visited, int startX, int startY, 
                           int width, int height, Blob blob) {
        java.util.Stack<int[]> stack = new java.util.Stack<>();
        stack.push(new int[]{startX, startY});
        
        while (!stack.isEmpty()) {
            int[] pos = stack.pop();
            int x = pos[0];
            int y = pos[1];
            
            if (x < 0 || x >= width || y < 0 || y >= height) continue;
            if (!binaryImage[y][x] || visited[y][x]) continue;
            
            visited[y][x] = true;
            blob.area++;
            blob.minX = Math.min(blob.minX, x);
            blob.maxX = Math.max(blob.maxX, x);
            blob.minY = Math.min(blob.minY, y);
            blob.maxY = Math.max(blob.maxY, y);
            
            stack.push(new int[]{x + 1, y});
            stack.push(new int[]{x - 1, y});
            stack.push(new int[]{x, y + 1});
            stack.push(new int[]{x, y - 1});
        }
    }

    private static class Blob {
        int area = 0;
        int minX = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
    }

    private Map<String, Object> analyzeGrainSize(BufferedImage grayImage) {
        Map<String, Object> result = new HashMap<>();
        
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        
        BufferedImage blurred = applyGaussianBlur(grayImage, 3);
        int threshold = calculateOtsuThreshold(blurred);
        
        boolean[][] binaryImage = new boolean[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = blurred.getRGB(x, y) & 0xFF;
                binaryImage[y][x] = gray > threshold;
            }
        }
        
        List<Blob> grains = findBlobs(binaryImage, width, height);
        grains.removeIf(blob -> blob.area < 8 || blob.area > 5000);
        
        if (!grains.isEmpty()) {
            double avgArea = grains.stream().mapToInt(b -> b.area).average().orElse(1);
            double avgSize = Math.sqrt(avgArea);
            
            double scaleFactor = 0.1;
            result.put("grainSize", Math.round(avgSize * scaleFactor * 100) / 100.0);
            result.put("grainCount", grains.size());
            
            double roundnessSum = 0;
            int validGrains = 0;
            for (Blob grain : grains) {
                int w = grain.maxX - grain.minX + 1;
                int h = grain.maxY - grain.minY + 1;
                if (w > 0 && h > 0) {
                    double aspectRatio = (double) Math.max(w, h) / Math.min(w, h);
                    roundnessSum += 1.0 / aspectRatio;
                    validGrains++;
                }
            }
            
            double avgRoundness = validGrains > 0 ? roundnessSum / validGrains : 0.5;
            result.put("roundness", Math.round(avgRoundness * 100) / 100.0);
            result.put("grainShape", classifyGrainShape(result.get("roundness")));
            result.put("sorting", classifySorting(grains));
        } else {
            result.put("grainSize", 0.0);
            result.put("grainCount", 0);
            result.put("roundness", 0.0);
            result.put("grainShape", "无法识别");
            result.put("sorting", "无法评估");
        }
        
        return result;
    }

    private BufferedImage applyCannyEdgeDetection(BufferedImage grayImage) {
        int width = grayImage.getWidth();
        int height = grayImage.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        
        BufferedImage blurred = applyGaussianBlur(grayImage, 3);
        
        int[][] gx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] gy = {{-1, -2, -1}, {0, 0, 0}, {1, 2, 1}};
        
        int[][] magnitude = new int[height][width];
        double[][] angle = new double[height][width];
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int sumX = 0, sumY = 0;
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int gray = blurred.getRGB(x + kx, y + ky) & 0xFF;
                        sumX += gray * gx[ky + 1][kx + 1];
                        sumY += gray * gy[ky + 1][kx + 1];
                    }
                }
                magnitude[y][x] = (int) Math.sqrt(sumX * sumX + sumY * sumY);
                angle[y][x] = Math.atan2(sumY, sumX);
            }
        }
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double theta = angle[y][x];
                int q = 255;
                
                if ((theta >= -Math.PI/8 && theta < Math.PI/8) || 
                    (theta >= 7*Math.PI/8 || theta < -7*Math.PI/8)) {
                    if (magnitude[y][x] <= magnitude[y][x-1] || 
                        magnitude[y][x] <= magnitude[y][x+1]) q = 0;
                } else if ((theta >= Math.PI/8 && theta < 3*Math.PI/8) || 
                           (theta >= -7*Math.PI/8 && theta < -5*Math.PI/8)) {
                    if (magnitude[y][x] <= magnitude[y-1][x+1] || 
                        magnitude[y][x] <= magnitude[y+1][x-1]) q = 0;
                } else if ((theta >= 3*Math.PI/8 && theta < 5*Math.PI/8) || 
                           (theta >= -5*Math.PI/8 && theta < -3*Math.PI/8)) {
                    if (magnitude[y][x] <= magnitude[y-1][x] || 
                        magnitude[y][x] <= magnitude[y+1][x]) q = 0;
                } else {
                    if (magnitude[y][x] <= magnitude[y-1][x-1] || 
                        magnitude[y][x] <= magnitude[y+1][x+1]) q = 0;
                }
                
                result.setRGB(x, y, q << 16 | q << 8 | q);
            }
        }
        
        return result;
    }

    private String classifyGrainShape(Object roundnessObj) {
        double roundness = ((Number) roundnessObj).doubleValue();
        if (roundness > 0.8) return "圆形";
        if (roundness > 0.6) return "次圆";
        if (roundness > 0.4) return "椭圆形";
        return "棱角状";
    }

    private String classifySorting(List<Blob> grains) {
        if (grains.size() < 5) return "无法评估";
        
        double[] areas = grains.stream().mapToDouble(b -> b.area).toArray();
        double mean = Arrays.stream(areas).average().orElse(0);
        double stdDev = Math.sqrt(Arrays.stream(areas).map(a -> Math.pow(a - mean, 2)).average().orElse(0));
        double cv = stdDev / mean;
        
        if (cv < 0.2) return "好";
        if (cv < 0.4) return "较好";
        if (cv < 0.6) return "中等";
        if (cv < 0.8) return "较差";
        return "差";
    }

    private Map<String, Object> detectFractures(BufferedImage grayImage) {
        Map<String, Object> result = new HashMap<>();
        
        BufferedImage edges = applyCannyEdgeDetection(grayImage);
        List<Line> lines = applyHoughLineDetection(edges);
        
        lines = mergeLines(lines);
        
        result.put("fractureCount", lines.size());
        
        if (!lines.isEmpty()) {
            double avgLength = lines.stream().mapToDouble(l -> l.length).average().orElse(0);
            double scaleFactor = 0.1;
            result.put("fractureLength", Math.round(avgLength * scaleFactor * 100) / 100.0);
            
            String orientation = classifyOrientation(lines);
            result.put("fractureOrientation", orientation);
        } else {
            result.put("fractureLength", 0.0);
            result.put("fractureOrientation", "无");
        }
        
        return result;
    }

    private List<Line> applyHoughLineDetection(BufferedImage edgeImage) {
        List<Line> lines = new ArrayList<>();
        int width = edgeImage.getWidth();
        int height = edgeImage.getHeight();
        
        int maxRho = (int) Math.sqrt(width * width + height * height);
        int numRhos = 2 * maxRho + 1;
        int numThetas = 180;
        
        int[][] accumulator = new int[numThetas][numRhos];
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if ((edgeImage.getRGB(x, y) & 0xFF) > 128) {
                    for (int thetaIdx = 0; thetaIdx < numThetas; thetaIdx++) {
                        double theta = Math.toRadians(thetaIdx);
                        int rho = (int) (x * Math.cos(theta) + y * Math.sin(theta));
                        int rhoIdx = rho + maxRho;
                        if (rhoIdx >= 0 && rhoIdx < numRhos) {
                            accumulator[thetaIdx][rhoIdx]++;
                        }
                    }
                }
            }
        }
        
        int threshold = Math.max(10, Math.min(width, height) / 20);
        boolean[][] visited = new boolean[numThetas][numRhos];
        
        for (int thetaIdx = 0; thetaIdx < numThetas; thetaIdx++) {
            for (int rhoIdx = 0; rhoIdx < numRhos; rhoIdx++) {
                if (accumulator[thetaIdx][rhoIdx] >= threshold && !visited[thetaIdx][rhoIdx]) {
                    int maxCount = accumulator[thetaIdx][rhoIdx];
                    int bestTheta = thetaIdx;
                    int bestRho = rhoIdx;
                    
                    for (int dt = -2; dt <= 2; dt++) {
                        for (int dr = -2; dr <= 2; dr++) {
                            int nt = thetaIdx + dt;
                            int nr = rhoIdx + dr;
                            if (nt >= 0 && nt < numThetas && nr >= 0 && nr < numRhos) {
                                if (accumulator[nt][nr] > maxCount) {
                                    maxCount = accumulator[nt][nr];
                                    bestTheta = nt;
                                    bestRho = nr;
                                }
                            }
                        }
                    }
                    
                    for (int dt = -5; dt <= 5; dt++) {
                        for (int dr = -10; dr <= 10; dr++) {
                            int nt = bestTheta + dt;
                            int nr = bestRho + dr;
                            if (nt >= 0 && nt < numThetas && nr >= 0 && nr < numRhos) {
                                visited[nt][nr] = true;
                            }
                        }
                    }
                    
                    double theta = Math.toRadians(bestTheta);
                    int rho = bestRho - maxRho;
                    
                    int x1, y1, x2, y2;
                    if (Math.abs(Math.sin(theta)) < 0.01) {
                        x1 = rho;
                        y1 = 0;
                        x2 = rho;
                        y2 = height - 1;
                    } else {
                        x1 = 0;
                        y1 = (int) ((rho - x1 * Math.cos(theta)) / Math.sin(theta));
                        x2 = width - 1;
                        y2 = (int) ((rho - x2 * Math.cos(theta)) / Math.sin(theta));
                    }
                    
                    x1 = Math.max(0, Math.min(width - 1, x1));
                    y1 = Math.max(0, Math.min(height - 1, y1));
                    x2 = Math.max(0, Math.min(width - 1, x2));
                    y2 = Math.max(0, Math.min(height - 1, y2));
                    
                    double length = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
                    if (length > 20) {
                        lines.add(new Line(x1, y1, x2, y2, length));
                    }
                }
            }
        }
        
        return lines;
    }

    private List<Line> mergeLines(List<Line> lines) {
        List<Line> merged = new ArrayList<>();
        boolean[] used = new boolean[lines.size()];
        
        for (int i = 0; i < lines.size(); i++) {
            if (used[i]) continue;
            
            Line current = lines.get(i);
            List<Line> toMerge = new ArrayList<>();
            toMerge.add(current);
            used[i] = true;
            
            boolean changed = true;
            while (changed) {
                changed = false;
                for (int j = 0; j < lines.size(); j++) {
                    if (used[j]) continue;
                    
                    Line other = lines.get(j);
                    if (current.isSimilar(other)) {
                        current = current.merge(other);
                        used[j] = true;
                        changed = true;
                    }
                }
            }
            
            merged.add(current);
        }
        
        return merged;
    }

    private String classifyOrientation(List<Line> lines) {
        if (lines.isEmpty()) return "无";
        
        int horizontal = 0, vertical = 0, diagonal = 0;
        
        for (Line line : lines) {
            double angle = Math.abs(line.getAngle());
            if (angle < 15 || angle > 165) horizontal++;
            else if (angle > 75 && angle < 105) vertical++;
            else diagonal++;
        }
        
        int max = Math.max(Math.max(horizontal, vertical), diagonal);
        if (max == horizontal) return "水平";
        if (max == vertical) return "垂直";
        return "斜向";
    }

    private static class Line {
        int x1, y1, x2, y2;
        double length;
        
        Line(int x1, int y1, int x2, int y2, double length) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.length = length;
        }
        
        double getAngle() {
            return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        }
        
        boolean isSimilar(Line other) {
            double angleDiff = Math.abs(this.getAngle() - other.getAngle());
            if (angleDiff > 30 && angleDiff < 150) return false;
            
            double thisMidX = (x1 + x2) / 2.0;
            double thisMidY = (y1 + y2) / 2.0;
            double otherMidX = (other.x1 + other.x2) / 2.0;
            double otherMidY = (other.y1 + other.y2) / 2.0;
            double dist = Math.sqrt(Math.pow(thisMidX - otherMidX, 2) + Math.pow(thisMidY - otherMidY, 2));
            
            return dist < 50;
        }
        
        Line merge(Line other) {
            int minX = Math.min(Math.min(x1, x2), Math.min(other.x1, other.x2));
            int maxX = Math.max(Math.max(x1, x2), Math.max(other.x1, other.x2));
            int minY = Math.min(Math.min(y1, y2), Math.min(other.y1, other.y2));
            int maxY = Math.max(Math.max(y1, y2), Math.max(other.y1, other.y2));
            
            double length = Math.sqrt((maxX - minX) * (maxX - minX) + (maxY - minY) * (maxY - minY));
            return new Line(minX, minY, maxX, maxY, length);
        }
    }
}
