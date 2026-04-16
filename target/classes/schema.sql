-- ==============================================
-- 海底地质岩心图文分析系统 - 数据库初始化脚本
-- ==============================================

-- 删除已有数据库（如果存在）
DROP DATABASE IF EXISTS ocean_analyse;

-- 创建数据库
CREATE DATABASE ocean_analyse DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE ocean_analyse;

-- ==============================================
-- 1. 岩心基本信息表
-- ==============================================
CREATE TABLE core_info (
                           core_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '岩心ID，自增主键',
                           sample_no VARCHAR(50) NOT NULL UNIQUE COMMENT '岩心编号，唯一标识',
                           basin_name VARCHAR(50) COMMENT '所属盆地名称',
                           stratum VARCHAR(30) COMMENT '地层信息',
                           lithology VARCHAR(30) COMMENT '岩性描述',
                           sedimentary_structure VARCHAR(20) COMMENT '沉积构造',
                           location VARCHAR(200) COMMENT '采样位置',
                           depth_from DECIMAL(10,2) COMMENT '起始深度(m)',
                           depth_to DECIMAL(10,2) COMMENT '终止深度(m)',
                           formation VARCHAR(100) COMMENT '地层名称',
                           color VARCHAR(50) COMMENT '颜色',
                           description TEXT COMMENT '描述信息',
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                           INDEX idx_sample_no (sample_no),
                           INDEX idx_basin_name (basin_name),
                           INDEX idx_lithology (lithology)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岩心基本信息表';

-- ==============================================
-- 2. 岩心图像表
-- ==============================================
CREATE TABLE core_image (
                            image_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图像ID，自增主键',
                            core_id BIGINT NOT NULL COMMENT '关联岩心ID',
                            image_path VARCHAR(255) COMMENT '图像存储路径',
                            upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
                            FOREIGN KEY (core_id) REFERENCES core_info(core_id) ON DELETE CASCADE,
                            INDEX idx_core_id (core_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='岩心图像表';

-- ==============================================
-- 3. 孔洞分析表
-- ==============================================
CREATE TABLE hole_analysis (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分析ID，自增主键',
                               core_sample_id BIGINT NOT NULL COMMENT '关联岩心ID',
                               holeDensity DECIMAL(10,4) COMMENT '孔洞密度',
                               averageDiameter DECIMAL(10,4) COMMENT '平均直径(mm)',
                               porosity DECIMAL(10,4) COMMENT '孔隙度(%)',
                               holeShape VARCHAR(50) COMMENT '孔洞形态',
                               analysisDate DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分析日期',
                               notes TEXT COMMENT '备注',
                               FOREIGN KEY (core_sample_id) REFERENCES core_info(core_id) ON DELETE CASCADE,
                               INDEX idx_core_sample_id (core_sample_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='孔洞分析表';

-- ==============================================
-- 4. 粒度分析表
-- ==============================================
CREATE TABLE grain_size_analysis (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分析ID，自增主键',
                                     core_sample_id BIGINT NOT NULL COMMENT '关联岩心ID',
                                     averageGrainSize DECIMAL(10,4) COMMENT '平均粒径(mm)',
                                     sortingCoefficient DECIMAL(10,4) COMMENT '分选系数',
                                     skewness DECIMAL(10,4) COMMENT '偏度',
                                     kurtosis DECIMAL(10,4) COMMENT '峰度',
                                     grainType VARCHAR(50) COMMENT '颗粒类型',
                                     analysisDate DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分析日期',
                                     notes TEXT COMMENT '备注',
                                     FOREIGN KEY (core_sample_id) REFERENCES core_info(core_id) ON DELETE CASCADE,
                                     INDEX idx_core_sample_id (core_sample_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='粒度分析表';

-- ==============================================
-- 5. 裂缝分析表
-- ==============================================
CREATE TABLE fracture_analysis (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分析ID，自增主键',
                                   core_sample_id BIGINT NOT NULL COMMENT '关联岩心ID',
                                   fractureDensity DECIMAL(10,4) COMMENT '裂缝密度',
                                   averageLength DECIMAL(10,4) COMMENT '平均长度(mm)',
                                   averageWidth DECIMAL(10,4) COMMENT '平均宽度(mm)',
                                   strike DECIMAL(10,2) COMMENT '走向(度)',
                                   dipAngle DECIMAL(10,2) COMMENT '倾角(度)',
                                   connectivity VARCHAR(20) COMMENT '连通性',
                                   analysisDate DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '分析日期',
                                   notes TEXT COMMENT '备注',
                                   FOREIGN KEY (core_sample_id) REFERENCES core_info(core_id) ON DELETE CASCADE,
                                   INDEX idx_core_sample_id (core_sample_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='裂缝分析表';

-- ==============================================
-- 6. 分析报告表
-- ==============================================
CREATE TABLE analysis_report (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '报告ID，自增主键',
                                 core_sample_id BIGINT NOT NULL COMMENT '关联岩心ID',
                                 reportName VARCHAR(200) COMMENT '报告名称',
                                 reportContent LONGTEXT COMMENT '报告内容',
                                 generatedAt DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
                                 FOREIGN KEY (core_sample_id) REFERENCES core_info(core_id) ON DELETE CASCADE,
                                 INDEX idx_core_sample_id (core_sample_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分析报告表';

-- ==============================================
-- 7. 综合分析结果表
-- ==============================================
CREATE TABLE analysis_result (
                                 result_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '结果ID，自增主键',
                                 core_id BIGINT NOT NULL COMMENT '关联岩心ID',
                                 analysis_type VARCHAR(20) COMMENT '分析类型(HOLE/GRAIN/FRACTURE)',
                                 hole_area FLOAT COMMENT '孔洞面积',
                                 hole_density FLOAT COMMENT '孔洞密度',
                                 hole_diameter_avg FLOAT COMMENT '孔洞平均直径',
                                 grain_size_avg FLOAT COMMENT '平均粒径',
                                 sorting_coefficient FLOAT COMMENT '分选系数',
                                 skewness FLOAT COMMENT '偏度',
                                 kurtosis FLOAT COMMENT '峰度',
                                 fracture_density FLOAT COMMENT '裂缝密度',
                                 fracture_length_avg FLOAT COMMENT '裂缝平均长度',
                                 fracture_width_avg FLOAT COMMENT '裂缝平均宽度',
                                 porosity FLOAT COMMENT '孔隙度',
                                 permeability FLOAT COMMENT '渗透率',
                                 FOREIGN KEY (core_id) REFERENCES core_info(core_id) ON DELETE CASCADE,
                                 INDEX idx_core_id (core_id),
                                 INDEX idx_analysis_type (analysis_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='综合分析结果表';

-- ==============================================
-- 8. 用户信息表
-- ==============================================
CREATE TABLE user_info (
                           user_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID，自增主键',
                           username VARCHAR(20) NOT NULL UNIQUE COMMENT '用户名，唯一登录账号',
                           password VARCHAR(255) NOT NULL COMMENT '加密密码',
                           user_role INT DEFAULT 1 COMMENT '用户角色(1普通/2教师/3管理员)',
                           created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                           INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ==============================================
-- 插入测试数据
-- ==============================================

-- 插入岩心样本数据
INSERT INTO core_info (sample_no, basin_name, stratum, lithology, sedimentary_structure, location, depth_from, depth_to, formation, color, description) VALUES
                                                                                                                                                            ('C001', '东海盆地', '白垩系', '灰岩', '层理构造', 'A区-1号井', 100.00, 150.00, '古近系', '深灰色', '测试岩心样本1'),
                                                                                                                                                            ('C002', '南海盆地', '第三系', '砂岩', '交错层理', 'B区-2号井', 200.00, 250.00, '新近系', '灰白色', '测试岩心样本2'),
                                                                                                                                                            ('C003', '渤海湾盆地', '侏罗系', '泥岩', '水平层理', 'C区-3号井', 300.00, 350.00, '中生界', '黑色', '测试岩心样本3'),
                                                                                                                                                            ('C004', '珠江口盆地', '第四系', '砾岩', '块状构造', 'D区-4号井', 150.00, 200.00, '新生界', '杂色', '测试岩心样本4'),
                                                                                                                                                            ('C005', '北部湾盆地', '三叠系', '页岩', '页理构造', 'E区-5号井', 400.00, 450.00, '古生界', '灰黑色', '测试岩心样本5');

-- 插入孔洞分析数据
INSERT INTO hole_analysis (core_sample_id, holeDensity, averageDiameter, porosity, holeShape) VALUES
                                                                                                  (1, 25.50, 0.85, 15.20, '圆形'),
                                                                                                  (2, 18.30, 1.20, 12.50, '椭圆形'),
                                                                                                  (3, 35.80, 0.65, 18.70, '不规则');

-- 插入粒度分析数据
INSERT INTO grain_size_analysis (core_sample_id, averageGrainSize, sortingCoefficient, skewness, kurtosis, grainType) VALUES
                                                                                                                          (1, 0.25, 1.58, 0.15, 2.25, '细砂'),
                                                                                                                          (2, 0.45, 1.32, 0.08, 2.10, '中砂'),
                                                                                                                          (4, 2.50, 2.15, 0.35, 2.80, '砾石');

-- 插入裂缝分析数据
INSERT INTO fracture_analysis (core_sample_id, fractureDensity, averageLength, averageWidth, strike, dipAngle, connectivity) VALUES
                                                                                                                                 (1, 8.50, 15.50, 0.15, 120.00, 65.00, '好'),
                                                                                                                                 (3, 12.30, 22.80, 0.22, 85.00, 70.00, '较好'),
                                                                                                                                 (5, 6.20, 18.30, 0.18, 150.00, 55.00, '一般');

-- 插入用户数据（密码已加密，原始密码：123456）
INSERT INTO user_info (username, password, user_role) VALUES
                                                          ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', 3),
                                                          ('teacher', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', 2),
                                                          ('user', '$2a$10$N9qo8uLOickgx2ZMRZoMye.IjzqAKL9xL5jvMFVdNJHvGCgTq/VEq', 1);

-- ==============================================
-- 数据库初始化完成
-- ==============================================
SELECT '数据库初始化完成！' AS result;