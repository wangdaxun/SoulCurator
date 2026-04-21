-- SoulCurator 数据库扩展：添加AI提示词字段
-- 版本：1.0
-- 日期：2026-04-21
-- 描述：为灵魂画像表添加AI提示词字段，支持AI能力扩展

-- ==================== 扩展现有表 ====================

-- 1. 为soul_portraits表添加AI提示词字段
ALTER TABLE soul_portraits 
ADD COLUMN IF NOT EXISTS ai_prompt TEXT;

COMMENT ON COLUMN soul_portraits.ai_prompt IS 'AI提示词，用于生成个性化内容或分析';

-- 2. 为soul_prototypes表添加AI提示词模板字段
ALTER TABLE soul_prototypes 
ADD COLUMN IF NOT EXISTS ai_prompt_template TEXT;

COMMENT ON COLUMN soul_prototypes.ai_prompt_template IS 'AI提示词模板，用于生成特定灵魂类型的AI提示';

-- 3. 为recommended_works表添加AI上下文字段
ALTER TABLE recommended_works 
ADD COLUMN IF NOT EXISTS ai_context TEXT;

COMMENT ON COLUMN recommended_works.ai_context IS 'AI上下文信息，用于生成个性化推荐理由';

-- ==================== 更新现有数据 ====================

-- 4. 为现有的灵魂原型添加AI提示词模板
UPDATE soul_prototypes 
SET ai_prompt_template = 
    CASE name
        WHEN '深度思考者' THEN 
            '你是一个深度思考者，具有以下特质：{traits}。' ||
            '你的思维倾向于{primary_dimension}，在{secondary_dimension}方面也有显著表现。' ||
            '基于你的灵魂画像，请生成个性化的内容或分析。'
        WHEN '感性体验者' THEN 
            '你是一个感性体验者，具有以下特质：{traits}。' ||
            '你的情感表达倾向于{primary_dimension}，在{secondary_dimension}方面也很敏感。' ||
            '基于你的灵魂画像，请生成富有情感共鸣的内容。'
        WHEN '冒险探索者' THEN 
            '你是一个冒险探索者，具有以下特质：{traits}。' ||
            '你的探索精神体现在{primary_dimension}，在{secondary_dimension}方面也很突出。' ||
            '基于你的灵魂画像，请生成激发好奇心和冒险精神的内容。'
        ELSE 
            '你是一个独特的灵魂，具有以下特质：{traits}。' ||
            '基于你的灵魂画像，请生成个性化的内容。'
    END
WHERE ai_prompt_template IS NULL;

-- 5. 为现有的推荐作品添加AI上下文
UPDATE recommended_works 
SET ai_context = 
    CASE 
        WHEN title = '《星际穿越》' THEN 
            '这部电影探讨了时间、爱和维度的主题，适合喜欢深度思考和哲学探索的灵魂。'
        WHEN title = 'Moonlight Sonata' THEN 
            '这首钢琴曲表达了月光下的宁静与沉思，适合情感丰富、喜欢内省的音乐爱好者。'
        WHEN title = '《尤利西斯》' THEN 
            '这部意识流小说展现了复杂的内心世界和思维流动，适合喜欢文学深度和思想挑战的读者。'
        WHEN title = '《海上钢琴师》' THEN 
            '这部电影讲述了一个钢琴天才在船上的传奇人生，适合浪漫主义者和艺术感知者。'
        WHEN title = '《小王子》' THEN 
            '这部童话探讨了成长、爱和责任，适合保持童心和追求真理的灵魂。'
        WHEN title = 'Clair de Lune' THEN 
            '这首德彪西的月光曲描绘了月光下的梦幻景象，适合喜欢印象派音乐和情感表达的人。'
        WHEN title = '《荒野生存》' THEN 
            '这部电影讲述了一个年轻人放弃一切追求自由的故事，适合冒险探索者和自由灵魂。'
        WHEN title = '《禅与摩托车维修艺术》' THEN 
            '这本书融合了哲学思考和公路旅行，适合喜欢深度思考和自我探索的读者。'
        WHEN title = '《勇敢的心》原声' THEN 
            '这首音乐表达了自由和勇气的主题，适合追求自由和具有冒险精神的人。'
        ELSE 
            '这是一个值得探索的作品，具有独特的艺术价值和文化意义。'
    END
WHERE ai_context IS NULL;

-- ==================== 创建索引 ====================

-- 6. 为ai_prompt字段创建GIN索引（支持全文搜索）
CREATE INDEX IF NOT EXISTS idx_portraits_ai_prompt ON soul_portraits USING GIN(to_tsvector('english', ai_prompt));

-- 7. 为ai_prompt_template字段创建索引
CREATE INDEX IF NOT EXISTS idx_prototypes_ai_prompt ON soul_prototypes USING GIN(to_tsvector('english', ai_prompt_template));

-- ==================== 完成消息 ====================

SELECT 'AI提示词字段添加完成！' AS message;
SELECT '新增字段：' AS fields_added;
SELECT '  - soul_portraits.ai_prompt (TEXT)' AS field1;
SELECT '  - soul_prototypes.ai_prompt_template (TEXT)' AS field2;
SELECT '  - recommended_works.ai_context (TEXT)' AS field3;
SELECT '' AS separator;
SELECT '数据更新：' AS data_updated;
SELECT '  - 为3个灵魂原型添加了AI提示词模板' AS update1;
SELECT '  - 为9个推荐作品添加了AI上下文' AS update2;
SELECT '' AS separator;
SELECT '索引创建：' AS indexes_created;
SELECT '  - idx_portraits_ai_prompt (全文搜索索引)' AS index1;
SELECT '  - idx_prototypes_ai_prompt (全文搜索索引)' AS index2;