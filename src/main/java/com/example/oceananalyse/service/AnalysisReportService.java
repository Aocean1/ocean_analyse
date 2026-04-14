package com.example.oceananalyse.service;

import com.example.oceananalyse.entity.AnalysisReport;
import com.example.oceananalyse.entity.CoreSample;
import com.example.oceananalyse.entity.FractureAnalysis;
import com.example.oceananalyse.entity.GrainSizeAnalysis;
import com.example.oceananalyse.entity.HoleAnalysis;
import com.example.oceananalyse.repository.AnalysisReportRepository;
import com.example.oceananalyse.repository.CoreSampleRepository;
import com.example.oceananalyse.repository.FractureAnalysisRepository;
import com.example.oceananalyse.repository.GrainSizeAnalysisRepository;
import com.example.oceananalyse.repository.HoleAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnalysisReportService {

    @Autowired
    private AnalysisReportRepository analysisReportRepository;

    @Autowired
    private CoreSampleRepository coreSampleRepository;

    @Autowired
    private HoleAnalysisRepository holeAnalysisRepository;

    @Autowired
    private GrainSizeAnalysisRepository grainSizeAnalysisRepository;

    @Autowired
    private FractureAnalysisRepository fractureAnalysisRepository;

    public AnalysisReport generateReport(Long coreSampleId) {
        CoreSample coreSample = coreSampleRepository.findById(coreSampleId)
                .orElseThrow(() -> new RuntimeException("岩心样本不存在，ID: " + coreSampleId));

        Optional<HoleAnalysis> holeAnalysis = holeAnalysisRepository.findByCoreSampleId(coreSampleId);
        Optional<GrainSizeAnalysis> grainSizeAnalysis = grainSizeAnalysisRepository.findByCoreSampleId(coreSampleId);
        Optional<FractureAnalysis> fractureAnalysis = fractureAnalysisRepository.findByCoreSampleId(coreSampleId);

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("=== 岩心样本基本信息 ===\n");
        reportContent.append("岩心编号: ").append(coreSample.getSampleNo()).append("\n");
        reportContent.append("盆地名称: ").append(coreSample.getBasinName()).append("\n");
        reportContent.append("采样位置: ").append(coreSample.getLocation()).append("\n");
        reportContent.append("深度范围: ").append(coreSample.getDepthFrom()).append("m - ").append(coreSample.getDepthTo()).append("m\n");
        reportContent.append("地层名称: ").append(coreSample.getFormation()).append("\n");
        reportContent.append("岩性: ").append(coreSample.getLithology()).append("\n");
        reportContent.append("颜色: ").append(coreSample.getColor()).append("\n");
        reportContent.append("描述: ").append(coreSample.getDescription()).append("\n\n");

        if (holeAnalysis.isPresent()) {
            HoleAnalysis ha = holeAnalysis.get();
            reportContent.append("=== 孔洞分析结果 ===\n");
            reportContent.append("孔洞密度: ").append(ha.getHoleDensity()).append(" 个/cm²\n");
            reportContent.append("平均直径: ").append(ha.getAvgDiameter()).append(" mm\n");
            reportContent.append("最大直径: ").append(ha.getMaxDiameter()).append(" mm\n");
            reportContent.append("最小直径: ").append(ha.getMinDiameter()).append(" mm\n");
            reportContent.append("孔洞形态: ").append(ha.getHoleShape()).append("\n");
            reportContent.append("孔洞类型: ").append(ha.getHoleType()).append("\n");
            reportContent.append("孔隙度: ").append(ha.getPorosity()).append("%\n");
            reportContent.append("分析方法: ").append(ha.getAnalysisMethod()).append("\n");
            reportContent.append("备注: ").append(ha.getAnalysisNote()).append("\n\n");
        }

        if (grainSizeAnalysis.isPresent()) {
            GrainSizeAnalysis ga = grainSizeAnalysis.get();
            reportContent.append("=== 粒度分析结果 ===\n");
            reportContent.append("平均粒径: ").append(ga.getAvgGrainSize()).append(" mm\n");
            reportContent.append("中值粒径: ").append(ga.getMedianSize()).append(" mm\n");
            reportContent.append("分选系数: ").append(ga.getSortingCoefficient()).append("\n");
            reportContent.append("偏度: ").append(ga.getSkewness()).append("\n");
            reportContent.append("峰度: ").append(ga.getKurtosis()).append("\n");
            reportContent.append("颗粒类型: ").append(ga.getGrainType()).append("\n");
            reportContent.append("磨圆度: ").append(ga.getRoundness()).append("\n");
            reportContent.append("分选程度: ").append(ga.getSortingDegree()).append("\n");
            reportContent.append("分析方法: ").append(ga.getAnalysisMethod()).append("\n");
            reportContent.append("备注: ").append(ga.getAnalysisNote()).append("\n\n");
        }

        if (fractureAnalysis.isPresent()) {
            FractureAnalysis fa = fractureAnalysis.get();
            reportContent.append("=== 裂缝分析结果 ===\n");
            reportContent.append("裂缝密度: ").append(fa.getFractureDensity()).append(" 条/m\n");
            reportContent.append("平均长度: ").append(fa.getAvgLength()).append(" cm\n");
            reportContent.append("平均宽度: ").append(fa.getAvgWidth()).append(" mm\n");
            reportContent.append("裂缝走向: ").append(fa.getFractureOrientation()).append("\n");
            reportContent.append("裂缝类型: ").append(fa.getFractureType()).append("\n");
            reportContent.append("充填状态: ").append(fa.getFillingStatus()).append("\n");
            reportContent.append("连通性: ").append(fa.getConnectivity()).append("\n");
            reportContent.append("分析方法: ").append(fa.getAnalysisMethod()).append("\n");
            reportContent.append("备注: ").append(fa.getAnalysisNote()).append("\n");
        }

        AnalysisReport report = new AnalysisReport();
        report.setCoreSample(coreSample);
        report.setReportTitle("岩心分析报告 - " + coreSample.getSampleNo());
        report.setReportContent(reportContent.toString());

        return analysisReportRepository.save(report);
    }

    public List<AnalysisReport> getAllReports() {
        return analysisReportRepository.findAllByOrderByGeneratedAtDesc();
    }

    public List<AnalysisReport> getReportsByCoreSampleId(Long coreSampleId) {
        return analysisReportRepository.findByCoreSampleId(coreSampleId);
    }

    public Optional<AnalysisReport> getReportById(Long id) {
        return analysisReportRepository.findById(id);
    }

    public void deleteReport(Long id) {
        analysisReportRepository.deleteById(id);
    }
}
