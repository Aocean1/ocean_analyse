CREATE DATABASE ocean_analyse CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ocean_analyse;
CREATE TABLE IF NOT EXISTS core_sample (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sample_no VARCHAR(50) NOT NULL UNIQUE COMMENT '岩心编号',
    basin_name VARCHAR(100) COMMENT '盆地名称',
    location VARCHAR(200) COMMENT '采样位置',
    depth_from DECIMAL(10,2) COMMENT '起始深度(m)',
    depth_to DECIMAL(10,2) COMMENT '终止深度(m)',
    formation VARCHAR(100) COMMENT '地层名称',
    lithology VARCHAR(100) COMMENT '岩性',
    color VARCHAR(50) COMMENT '颜色',
    description TEXT COMMENT '描述',
    image_path VARCHAR(500) COMMENT '图像路径',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岩心样本表';

CREATE TABLE IF NOT EXISTS hole_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    core_sample_id BIGINT NOT NULL,
    hole_density DECIMAL(10,4) COMMENT '孔洞密度(个/cm²)',
    avg_diameter DECIMAL(10,4) COMMENT '平均直径(mm)',
    max_diameter DECIMAL(10,4) COMMENT '最大直径(mm)',
    min_diameter DECIMAL(10,4) COMMENT '最小直径(mm)',
    hole_shape VARCHAR(50) COMMENT '孔洞形态',
    hole_type VARCHAR(50) COMMENT '孔洞类型',
    porosity DECIMAL(10,4) COMMENT '孔隙度(%)',
    analysis_method VARCHAR(100) COMMENT '分析方法',
    analysis_note TEXT COMMENT '分析备注',
    FOREIGN KEY (core_sample_id) REFERENCES core_sample(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='孔洞分析表';

CREATE TABLE IF NOT EXISTS grain_size_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    core_sample_id BIGINT NOT NULL,
    avg_grain_size DECIMAL(10,4) COMMENT '平均粒径(mm)',
    median_size DECIMAL(10,4) COMMENT '中值粒径(mm)',
    sorting_coefficient DECIMAL(10,4) COMMENT '分选系数',
    skewness DECIMAL(10,4) COMMENT '偏度',
    kurtosis DECIMAL(10,4) COMMENT '峰度',
    grain_type VARCHAR(100) COMMENT '颗粒类型',
    roundness VARCHAR(50) COMMENT '磨圆度',
    sorting_degree VARCHAR(50) COMMENT '分选程度',
    analysis_method VARCHAR(100) COMMENT '分析方法',
    analysis_note TEXT COMMENT '分析备注',
    FOREIGN KEY (core_sample_id) REFERENCES core_sample(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='粒度分析表';

CREATE TABLE IF NOT EXISTS fracture_analysis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    core_sample_id BIGINT NOT NULL,
    fracture_density DECIMAL(10,4) COMMENT '裂缝密度(条/m)',
    avg_length DECIMAL(10,4) COMMENT '平均长度(cm)',
    avg_width DECIMAL(10,4) COMMENT '平均宽度(mm)',
    fracture_orientation VARCHAR(100) COMMENT '裂缝走向',
    fracture_type VARCHAR(50) COMMENT '裂缝类型',
    filling_status VARCHAR(50) COMMENT '充填状态',
    connectivity VARCHAR(50) COMMENT '连通性',
    analysis_method VARCHAR(100) COMMENT '分析方法',
    analysis_note TEXT COMMENT '分析备注',
    FOREIGN KEY (core_sample_id) REFERENCES core_sample(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='裂缝分析表';

CREATE TABLE IF NOT EXISTS analysis_report (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    core_sample_id BIGINT NOT NULL,
    report_title VARCHAR(200) COMMENT '报告标题',
    report_content TEXT COMMENT '报告内容',
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (core_sample_id) REFERENCES core_sample(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分析报告表';
