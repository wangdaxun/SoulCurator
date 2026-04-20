-- SoulCurator 数据库扩展脚本 - 灵魂画像生成功能
-- 版本：1.1
-- 日期：2026-04-20
-- 描述：扩展数据库以支持规则引擎生成的灵魂画像

-- ==================== 扩展现有表结构 ====================

-- 1. 扩展 recommended_works 表，添加灵魂类型匹配字段
ALTER TABLE recommended_works 
ADD COLUMN IF NOT EXISTS soul_types VARCHAR(50)[],  -- 匹配的灵魂类型数组
ADD COLUMN IF NOT EXISTS icon VARCHAR(50),           -- 图标名称（前端使用）
ADD COLUMN IF NOT EXISTS color_hex VARCHAR(7),       -- 颜色代码（十六进制）
ADD COLUMN IF NOT EXISTS display_order INTEGER DEFAULT 0;  -- 显示顺序

COMMENT ON COLUMN recommended_works.soul_types IS '匹配的灵魂类型数组，如：["深度思考者", "感性体验者"]';
COMMENT ON COLUMN recommended_works.icon IS '图标名称，前端使用的图标标识';
COMMENT ON COLUMN recommended_works.color_hex IS '颜色代码，十六进制格式，如：#8b5cf6';
COMMENT ON COLUMN recommended_works.display_order IS '显示顺序，数值越小越靠前';

-- 2. 扩展 soul_portraits 表，添加规则引擎相关字段
ALTER TABLE soul_portraits 
ADD COLUMN IF NOT EXISTS traits JSONB,               -- 特质列表（JSON数组）
ADD COLUMN IF NOT EXISTS quote JSONB,                -- 引用信息（JSON对象）
ADD COLUMN IF NOT EXISTS metadata JSONB,             -- 元数据（JSON对象）
ADD COLUMN IF NOT EXISTS generation_method VARCHAR(20) DEFAULT 'rule_engine',  -- 生成方法：rule_engine/ai
ADD COLUMN IF NOT EXISTS match_score DECIMAL(5,2);   -- 匹配度分数（0-100）

COMMENT ON COLUMN soul_portraits.traits IS '特质列表，JSON数组格式：[{"name": "深度内省", "icon": "moon", "color": "#8b5cf6"}]';
COMMENT ON COLUMN soul_portraits.quote IS '引用信息，JSON对象格式：{"text": "不要温顺地走进那个良夜", "author": "狄兰·托马斯"}';
COMMENT ON COLUMN soul_portraits.metadata IS '元数据，JSON对象格式：{"generatedAt": "2026-04-20T18:00:00+08:00", "soulId": "0x7E...42C", "node": "深空节点"}';
COMMENT ON COLUMN soul_portraits.generation_method IS '生成方法：rule_engine（规则引擎）/ ai（AI生成）';
COMMENT ON COLUMN soul_portraits.match_score IS '匹配度分数，表示用户选择与画像原型的匹配程度';

-- ==================== 创建配置表 ====================

-- 3. 灵魂原型配置表（存储规则引擎的配置）
CREATE TABLE IF NOT EXISTS soul_prototypes (
    id BIGSERIAL PRIMARY KEY,
    
    -- 原型信息
    name VARCHAR(50) UNIQUE NOT NULL,        -- 原型名称，如：深度思考者
    description_template TEXT NOT NULL,      -- 描述模板，支持变量替换
    icon VARCHAR(50),                        -- 默认图标
    color_hex VARCHAR(7) DEFAULT '#8b5cf6',  -- 默认颜色
    
    -- 维度权重配置（JSON格式）
    dimension_weights JSONB NOT NULL,        -- 各维度的权重阈值，如：{"introspection": 8, "logic": 6}
    
    -- 特质配置
    traits JSONB NOT NULL,                   -- 特质列表，JSON数组格式
    
    -- 引用配置
    quotes JSONB,                            -- 引用列表，JSON数组格式
    
    -- 状态
    is_active BOOLEAN DEFAULT TRUE,
    display_order INTEGER DEFAULT 0,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

COMMENT ON TABLE soul_prototypes IS '灵魂原型配置表，存储规则引擎的配置';
COMMENT ON COLUMN soul_prototypes.dimension_weights IS '维度权重配置，JSON格式：{"introspection": 8, "logic": 6, "art": 4}';
COMMENT ON COLUMN soul_prototypes.traits IS '特质列表，JSON数组格式：[{"name": "深度内省", "icon": "moon"}, {"name": "理性极客", "icon": "cpu"}]';
COMMENT ON COLUMN soul_prototypes.quotes IS '引用列表，JSON数组格式：[{"text": "不要温顺地走进那个良夜", "author": "狄兰·托马斯"}]';

-- 4. 描述模板变量表（支持动态描述生成）
CREATE TABLE IF NOT EXISTS description_variables (
    id BIGSERIAL PRIMARY KEY,
    
    -- 变量信息
    variable_key VARCHAR(50) NOT NULL,       -- 变量键，如：${adjective}
    variable_type VARCHAR(20) NOT NULL,      -- 变量类型：adjective/time/theme
    
    -- 变量值
    values JSONB NOT NULL,                   -- 变量值数组，JSON格式
    
    -- 关联维度
    dimension VARCHAR(50),                   -- 关联的维度（可选）
    
    -- 状态
    is_active BOOLEAN DEFAULT TRUE,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 约束
    UNIQUE(variable_key, variable_type)
);

COMMENT ON TABLE description_variables IS '描述模板变量表，支持动态描述生成';
COMMENT ON COLUMN description_variables.variable_key IS '变量键，在模板中使用，如：${adjective}';
COMMENT ON COLUMN description_variables.variable_type IS '变量类型：adjective（形容词）/ time（时间）/ theme（主题）';
COMMENT ON COLUMN description_variables.values IS '变量值数组，JSON格式：["静谧的", "深邃的", "神秘的"]';

-- ==================== 插入初始数据 ====================

-- 5. 插入灵魂原型配置数据
INSERT INTO soul_prototypes (name, description_template, icon, color_hex, dimension_weights, traits, quotes, display_order) VALUES
(
    '深度思考者',
    '你的灵魂在${adjective}的星空下共鸣，于${time}之时寻找${theme}的逻辑与诗意。',
    'brain',
    '#8b5cf6',
    '{"introspection": 8, "logic": 6, "art": 4, "adventure": 2}',
    '[{"name": "深度内省", "icon": "moon"}, {"name": "理性极客", "icon": "cpu"}, {"name": "孤独探索者", "icon": "compass"}, {"name": "隐秘艺术家", "icon": "palette"}]',
    '[{"text": "不要温顺地走进那个良夜，激情不能被消沉的暮色所屈服。", "author": "狄兰·托马斯"}, {"text": "我思故我在。", "author": "笛卡尔"}, {"text": "未经审视的人生不值得度过。", "author": "苏格拉底"}]',
    1
),
(
    '感性体验者',
    '你的心灵在${adjective}的色彩中舞蹈，于${time}的旋律里感受${theme}的温度与颤动。',
    'heart',
    '#ef4444',
    '{"emotion": 8, "art": 7, "introspection": 5, "logic": 3}',
    '[{"name": "情感丰沛", "icon": "heart"}, {"name": "艺术感知", "icon": "music"}, {"name": "直觉敏锐", "icon": "eye"}, {"name": "浪漫主义", "icon": "star"}]',
    '[{"text": "心有猛虎，细嗅蔷薇。", "author": "西格里夫·萨松"}, {"text": "人生如逆旅，我亦是行人。", "author": "苏轼"}, {"text": "爱是恒久忍耐，又有恩慈。", "author": "圣经"}]',
    2
),
(
    '冒险探索者',
    '你的精神在${adjective}的山巅翱翔，于${time}的未知中追寻${theme}的边界与可能。',
    'compass',
    '#10b981',
    '{"adventure": 9, "curiosity": 7, "logic": 5, "introspection": 3}',
    '[{"name": "无畏冒险", "icon": "mountain"}, {"name": "好奇求知", "icon": "globe"}, {"name": "开拓精神", "icon": "rocket"}, {"name": "自由灵魂", "icon": "wind"}]',
    '[{"text": "路漫漫其修远兮，吾将上下而求索。", "author": "屈原"}, {"text": "不要走在我后面，我可能不会引路；不要走在我前面，我可能不会跟随；请走在我的身边，做我的朋友。", "author": "阿尔贝·加缪"}, {"text": "生活就像一盒巧克力，你永远不知道下一颗是什么味道。", "author": "阿甘正传"}]',
    3
),
(
    '和谐平衡者',
    '你的存在在${adjective}的韵律中流动，于${time}的平衡里融合${theme}的对立与统一。',
    'balance',
    '#f59e0b',
    '{"balance": 8, "introspection": 6, "emotion": 6, "logic": 5}',
    '[{"name": "中庸之道", "icon": "balance"}, {"name": "内在和谐", "icon": "yin-yang"}, {"name": "包容理解", "icon": "handshake"}, {"name": "智慧沉淀", "icon": "book"}]',
    '[{"text": "中庸之为德也，其至矣乎！", "author": "孔子"}, {"text": "万物负阴而抱阳，冲气以为和。", "author": "老子"}, {"text": "真正的智慧在于知道自己的无知。", "author": "苏格拉底"}]',
    4
);

-- 6. 插入描述模板变量数据
INSERT INTO description_variables (variable_key, variable_type, values, dimension) VALUES
-- 形容词变量
('${adjective}', 'adjective', '["静谧的", "深邃的", "神秘的", "璀璨的", "朦胧的", "浩瀚的", "幽暗的", "绚烂的"]', NULL),
-- 时间变量
('${time}', 'time', '["万籁俱寂", "晨光熹微", "暮色苍茫", "午夜时分", "午后慵懒", "黄昏时刻", "黎明前夕", "永恒瞬间"]', NULL),
-- 主题变量
('${theme}', 'theme', '["宇宙", "生命", "时间", "存在", "意识", "真理", "美", "爱"]', NULL),
-- 维度相关的形容词
('${adjective_introspection}', 'adjective', '["内省的", "沉思的", "自省的", "冥想的", "反思的"]', 'introspection'),
('${adjective_emotion}', 'adjective', '["感性的", "热情的", "温柔的", "激烈的", "细腻的"]', 'emotion'),
('${adjective_adventure}', 'adjective', '["冒险的", "勇敢的", "自由的", "探索的", "开拓的"]', 'adventure');

-- 7. 更新 recommended_works 表，添加灵魂类型匹配
-- 先确保有示例数据，如果没有则插入
INSERT INTO recommended_works (title, type, description, tags, soul_types, icon, color_hex, cover_image_url, display_order)
SELECT * FROM (VALUES
    ('《星际穿越》', 'movie', '唯有爱与时间能超越维度。你的灵魂与这部史诗在时空奇点交汇。', 
     ARRAY['科幻', '亲情', '时间'], ARRAY['深度思考者', '冒险探索者'], 
     'rocket', '#8b5cf6', 'https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&q=80&w=600', 1),
    ('Moonlight Sonata', 'music', '贝多芬的月光奏鸣曲，在这寂静时刻，是脑海里最完美的背景乐。', 
     ARRAY['古典', '钢琴', '宁静'], ARRAY['深度思考者', '感性体验者'], 
     'music', '#8b5cf6', 'https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&q=80&w=600', 2),
    ('《尤利西斯》', 'book', '意识流的巅峰之作，正如你跳跃而深刻的思维，难以被定义。', 
     ARRAY['文学', '意识流', '深刻'], ARRAY['深度思考者'], 
     'book', '#8b5cf6', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&q=80&w=600', 3),
    ('《海上钢琴师》', 'movie', '在有限的琴键上，奏出无限的音乐。你的灵魂与1900一样，选择留在自己的船上。', 
     ARRAY['音乐', '人生', '选择'], ARRAY['感性体验者', '深度思考者'], 
     'piano', '#ef4444', 'https://images.unsplash.com/photo-1511379938547-c1f69419868d?auto=format&fit=crop&q=80&w=600', 4),
    ('《小王子》', 'book', '所有的大人都曾经是小孩，虽然只有少数人记得。', 
     ARRAY['童话', '哲学', '成长'], ARRAY['感性体验者', '和谐平衡者'], 
     'star', '#ef4444', 'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=600', 5),
    ('《荒野生存》', 'movie', '有些人觉得他们不需要任何东西，有些人觉得他们需要全世界。', 
     ARRAY['冒险', '自由', '自然'], ARRAY['冒险探索者'], 
     'mountain', '#10b981', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&q=80&w=600', 6),
    ('《禅与摩托车维修艺术》', 'book', '我们常常太忙而没有时间好好聊聊，结果日复一日地过着无聊的生活。', 
     ARRAY['哲学', '旅行', '技术'], ARRAY['深度思考者', '和谐平衡者'], 
     'motorcycle', '#f59e0b', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&q=80&w=600', 7)
) AS new_works(title, type, description, tags, soul_types, icon, color_hex, cover_image_url, display_order)
WHERE NOT EXISTS (SELECT 1 FROM recommended_works WHERE title = new_works.title AND type = new_works.type);

-- ==================== 创建索引优化 ====================

-- 8. 为灵魂原型表创建索引
CREATE INDEX IF NOT EXISTS idx_prototypes_active ON soul_prototypes(is_active);
CREATE INDEX IF NOT EXISTS idx_prototypes_order ON soul_prototypes(display_order);
CREATE INDEX IF NOT EXISTS idx_prototypes_dimension_weights ON soul_prototypes USING GIN(dimension_weights);

-- 9. Create indexes for description_variables table
CREATE INDEX IF NOT EXISTS idx_variables_type ON description_variables(variable_type);
CREATE INDEX IF NOT EXISTS idx_variables_dimension ON description_variables(dimension);

-- 10. 为灵魂画像查询创建索引
CREATE INDEX IF NOT EXISTS idx_portraits_generation_method ON soul_portraits(generation_method);
CREATE INDEX IF NOT EXISTS idx_portraits_match_score ON soul_portraits(match_score DESC);

-- 11. 为推荐作品查询创建索引
CREATE INDEX IF NOT EXISTS idx_works_soul_types ON recommended_works USING GIN(soul_types);
CREATE INDEX IF NOT EXISTS idx_works_display_order ON recommended_works(display_order);

-- ==================== 更新注释 ====================

COMMENT ON TABLE soul_prototypes IS '灵魂原型配置表，用于规则引擎匹配用户选择生成灵魂画像';
COMMENT ON TABLE description_variables IS '描述模板变量表，用于动态生成个性化的灵魂描述';

-- ==================== 完成提示 ====================

SELECT '数据库扩展完成！' AS message;
SELECT '新增表：soul_prototypes, description_variables' AS tables_added;
SELECT '扩展表：recommended_works, soul_portraits' AS tables_extended;
SELECT '插入初始数据：4个灵魂原型，6个变量组，7个推荐作品' AS data_inserted;