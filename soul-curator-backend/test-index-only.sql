-- 只测试索引创建，不创建表
-- 先确保表已存在

-- 测试1: 创建简单索引
CREATE INDEX IF NOT EXISTS test_idx_1 ON soul_portraits(generation_method);

-- 测试2: 创建带DESC的索引
CREATE INDEX IF NOT EXISTS test_idx_2 ON soul_portraits(match_score DESC);

-- 测试3: 创建GIN索引
-- 先检查表是否存在
DO $$
BEGIN
    IF EXISTS (SELECT FROM information_schema.tables WHERE table_name = 'recommended_works') THEN
        CREATE INDEX IF NOT EXISTS test_idx_3 ON recommended_works USING GIN(soul_types);
    END IF;
END$$;

SELECT 'Index test completed' AS result;