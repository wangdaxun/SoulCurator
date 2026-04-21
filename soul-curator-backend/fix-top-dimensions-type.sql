-- 修复soul_portraits表top_dimensions字段类型
-- 版本：1.0
-- 日期：2026-04-21
-- 描述：将top_dimensions字段从character varying[]改为jsonb类型

-- ==================== 修改表结构 ====================

-- 1. 备份现有数据（如果有的话）
-- 注意：如果表中有数据，需要先转换
CREATE TABLE IF NOT EXISTS soul_portraits_backup_20260421 AS 
SELECT * FROM soul_portraits;

-- 2. 修改top_dimensions字段类型为jsonb
-- 先删除现有数据（如果有转换问题）
-- 注意：这会导致数据丢失，仅用于开发环境
-- 在生产环境中需要更复杂的迁移脚本

-- 方法1：直接修改类型（如果数据可以自动转换）
-- ALTER TABLE soul_portraits 
-- ALTER COLUMN top_dimensions TYPE jsonb USING top_dimensions::jsonb;

-- 方法2：先设置为NULL，再修改类型（开发环境推荐）
ALTER TABLE soul_portraits 
ALTER COLUMN top_dimensions DROP NOT NULL;

UPDATE soul_portraits 
SET top_dimensions = NULL 
WHERE top_dimensions IS NOT NULL;

ALTER TABLE soul_portraits 
ALTER COLUMN top_dimensions TYPE jsonb USING top_dimensions::jsonb;

-- 3. 更新字段注释
COMMENT ON COLUMN soul_portraits.top_dimensions IS '前几个维度（JSONB数组格式）';

-- ==================== 验证修改 ====================

-- 4. 检查表结构
SELECT 
    column_name, 
    data_type,
    udt_name,
    is_nullable,
    character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'soul_portraits'
AND column_name = 'top_dimensions';

-- 5. 测试插入JSONB数据
INSERT INTO soul_portraits (
    session_id, soul_type, description, 
    top_dimensions, total_questions, completed_questions,
    total_time_seconds, created_at, updated_at
) VALUES (
    'test_session_' || EXTRACT(EPOCH FROM NOW()),
    '测试类型',
    '测试描述',
    '["introspection", "logic", "art"]'::jsonb,
    5, 3, 300,
    NOW(), NOW()
) ON CONFLICT DO NOTHING;

-- 6. 检查插入的数据
SELECT 
    session_id, 
    soul_type,
    top_dimensions,
    jsonb_typeof(top_dimensions) as json_type
FROM soul_portraits 
WHERE session_id LIKE 'test_session_%'
ORDER BY created_at DESC 
LIMIT 1;

-- 7. 清理测试数据
DELETE FROM soul_portraits 
WHERE session_id LIKE 'test_session_%';

-- ==================== 完成消息 ====================

SELECT 'soul_portraits表top_dimensions字段已修改为jsonb类型' AS message;
SELECT '现在可以存储JSON格式的维度数据' AS description;
SELECT '注意：需要重启应用使Hibernate识别修改' AS note;