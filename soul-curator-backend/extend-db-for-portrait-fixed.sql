-- SoulCurator database extension for portrait generation
-- Version: 1.1
-- Date: 2026-04-20

-- ==================== Extend existing tables ====================

-- 1. Extend recommended_works table
ALTER TABLE recommended_works 
ADD COLUMN IF NOT EXISTS soul_types VARCHAR(50)[],  
ADD COLUMN IF NOT EXISTS icon VARCHAR(50),           
ADD COLUMN IF NOT EXISTS color_hex VARCHAR(7),       
ADD COLUMN IF NOT EXISTS display_order INTEGER DEFAULT 0;

-- 2. Extend soul_portraits table
ALTER TABLE soul_portraits 
ADD COLUMN IF NOT EXISTS traits JSONB,               
ADD COLUMN IF NOT EXISTS quote JSONB,                
ADD COLUMN IF NOT EXISTS metadata JSONB,             
ADD COLUMN IF NOT EXISTS generation_method VARCHAR(20) DEFAULT 'rule_engine',
ADD COLUMN IF NOT EXISTS match_score DECIMAL(5,2);

-- ==================== Create new tables ====================

-- 3. Soul prototypes configuration table
CREATE TABLE IF NOT EXISTS soul_prototypes (
    id BIGSERIAL PRIMARY KEY,
    
    -- Prototype info
    name VARCHAR(50) UNIQUE NOT NULL,
    description_template TEXT NOT NULL,
    icon VARCHAR(50),
    color_hex VARCHAR(7) DEFAULT '#8b5cf6',
    
    -- Dimension weights
    dimension_weights JSONB NOT NULL,
    
    -- Traits configuration
    traits JSONB NOT NULL,
    
    -- Quotes configuration
    quotes JSONB,
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    display_order INTEGER DEFAULT 0,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Description variables table
CREATE TABLE IF NOT EXISTS description_variables (
    id BIGSERIAL PRIMARY KEY,
    
    -- Variable info
    variable_key VARCHAR(50) NOT NULL,
    variable_type VARCHAR(20) NOT NULL,
    
    -- Variable values
    values JSONB NOT NULL,
    
    -- Related dimension
    dimension VARCHAR(50),
    
    -- Status
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Timestamps
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- Constraints
    UNIQUE(variable_key, variable_type)
);

-- ==================== Insert initial data ====================

-- 5. Insert soul prototypes
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
);

-- 6. Insert description variables
INSERT INTO description_variables (variable_key, variable_type, values, dimension) VALUES
('${adjective}', 'adjective', '["静谧的", "深邃的", "神秘的", "璀璨的", "朦胧的", "浩瀚的", "幽暗的", "绚烂的"]', NULL),
('${time}', 'time', '["万籁俱寂", "晨光熹微", "暮色苍茫", "午夜时分", "午后慵懒", "黄昏时刻", "黎明前夕", "永恒瞬间"]', NULL),
('${theme}', 'theme', '["宇宙", "生命", "时间", "存在", "意识", "真理", "美", "爱"]', NULL);

-- 7. Update recommended_works with sample data
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
     'book', '#8b5cf6', 'https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&q=80&w=600', 3)
) AS new_works(title, type, description, tags, soul_types, icon, color_hex, cover_image_url, display_order)
WHERE NOT EXISTS (SELECT 1 FROM recommended_works WHERE title = new_works.title AND type = new_works.type);

-- ==================== Create indexes ====================

-- 8. Indexes for soul_prototypes
CREATE INDEX IF NOT EXISTS idx_prototypes_active ON soul_prototypes(is_active);
CREATE INDEX IF NOT EXISTS idx_prototypes_order ON soul_prototypes(display_order);
CREATE INDEX IF NOT EXISTS idx_prototypes_dimension_weights ON soul_prototypes USING GIN(dimension_weights);

-- 9. Indexes for description_variables
CREATE INDEX IF NOT EXISTS idx_variables_type ON description_variables(variable_type);
CREATE INDEX IF NOT EXISTS idx_variables_dimension ON description_variables(dimension);

-- 10. Indexes for soul_portraits
CREATE INDEX IF NOT EXISTS idx_portraits_generation_method ON soul_portraits(generation_method);
CREATE INDEX IF NOT EXISTS idx_portraits_match_score ON soul_portraits(match_score DESC);

-- 11. Indexes for recommended_works
CREATE INDEX IF NOT EXISTS idx_works_soul_types ON recommended_works USING GIN(soul_types);
CREATE INDEX IF NOT EXISTS idx_works_display_order ON recommended_works(display_order);

-- ==================== Completion message ====================

SELECT 'Database extension completed successfully!' AS message;
SELECT 'New tables: soul_prototypes, description_variables' AS tables_added;
SELECT 'Extended tables: recommended_works, soul_portraits' AS tables_extended;
SELECT 'Initial data: 3 soul prototypes, 3 variable groups, 3 recommended works' AS data_inserted;