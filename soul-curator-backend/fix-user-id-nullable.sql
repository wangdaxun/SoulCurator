-- 修复soul_portraits表user_id字段允许为NULL
-- 版本：1.0
-- 日期：2026-04-21
-- 描述：修改soul_portraits表的user_id字段，允许为NULL，支持匿名用户

-- ==================== 修改表结构 ====================

-- 1. 修改soul_portraits表的user_id字段，允许为NULL
ALTER TABLE soul_portraits 
ALTER COLUMN user_id DROP NOT NULL;

-- 2. 更新外键约束，允许user_id为NULL时跳过外键检查
-- PostgreSQL会自动处理，当user_id为NULL时不会触发外键约束

-- 3. 更新现有数据（如果有的话）
-- 将现有的NULL user_id设置为NULL（不需要操作）

-- ==================== 验证修改 ====================

-- 4. 检查表结构
SELECT 
    column_name, 
    is_nullable,
    data_type,
    character_maximum_length
FROM information_schema.columns 
WHERE table_name = 'soul_portraits'
AND column_name = 'user_id';

-- 5. 检查约束
SELECT 
    tc.table_name, 
    kcu.column_name, 
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name,
    tc.constraint_name
FROM information_schema.table_constraints AS tc 
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
    AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY' 
    AND tc.table_name = 'soul_portraits'
    AND kcu.column_name = 'user_id';

-- ==================== 更新实体类注释 ====================

-- 6. 更新字段注释
COMMENT ON COLUMN soul_portraits.user_id IS '用户ID，可为NULL表示匿名用户';

-- ==================== 完成消息 ====================

SELECT 'soul_portraits表user_id字段已修改为允许NULL值' AS message;
SELECT '现在支持匿名用户的灵魂画像存储' AS description;
SELECT '注意：需要重启应用使Hibernate识别修改' AS note;