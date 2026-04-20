-- 简单测试：只创建核心表，不插入数据
-- 用于验证SQL语法

-- 1. 创建灵魂原型表
CREATE TABLE IF NOT EXISTS soul_prototypes_test (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description_template TEXT NOT NULL,
    dimension_weights JSONB NOT NULL,
    traits JSONB NOT NULL,
    quotes JSONB,
    is_active BOOLEAN DEFAULT TRUE
);

-- 2. 创建描述变量表
CREATE TABLE IF NOT EXISTS description_variables_test (
    id BIGSERIAL PRIMARY KEY,
    variable_key VARCHAR(50) NOT NULL,
    variable_type VARCHAR(20) NOT NULL,
    values JSONB NOT NULL,
    UNIQUE(variable_key, variable_type)
);

-- 3. 创建索引
CREATE INDEX IF NOT EXISTS idx_prototypes_test_active ON soul_prototypes_test(is_active);
CREATE INDEX IF NOT EXISTS idx_variables_test_type ON description_variables_test(variable_type);

-- 4. 测试插入数据
INSERT INTO soul_prototypes_test (name, description_template, dimension_weights, traits) VALUES
(
    '测试原型',
    '你的灵魂在${adjective}的星空下共鸣。',
    '{"test": 5}',
    '[{"name": "测试特质", "icon": "test"}]'
);

INSERT INTO description_variables_test (variable_key, variable_type, values) VALUES
('${adjective}', 'adjective', '["美丽的", "神秘的"]');

-- 5. 查询验证
SELECT '表创建成功！' AS message;
SELECT * FROM soul_prototypes_test;
SELECT * FROM description_variables_test;

-- 6. 清理（可选）
-- DROP TABLE IF EXISTS description_variables_test;
-- DROP TABLE IF EXISTS soul_prototypes_test;