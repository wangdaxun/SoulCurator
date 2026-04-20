-- SoulCurator 灵魂之门入口初始化数据
-- 生成时间: 2026-04-16
-- 描述: 初始化4个灵魂之门入口配置

-- 清空现有数据（可选）
-- TRUNCATE TABLE soul_gateways CASCADE;

-- 插入电影之门
INSERT INTO soul_gateways (
    id,
    name,
    description,
    color_hex,
    icon,
    gateway_type,
    category,
    display_order,
    is_active,
    popularity_score,
    ai_prompt,
    target_dimensions,
    created_at,
    updated_at
) VALUES (
    'movie_gateway',
    '电影之门',
    '在光影编织的梦境里\n寻找另一个自己',
    '#8B5CF6', -- purple
    'clapperboard',
    'movie',
    'entertainment',
    1,
    true,
    0,
    '根据用户选择的电影之门，生成5个关于电影偏好的问题，涵盖情感共鸣、叙事风格、视觉审美、主题深度和观影场景等方面。',
    '{"visual": 0.8, "narrative": 0.9, "emotional": 0.7, "aesthetic": 0.8}',
    NOW(),
    NOW()
);

-- 插入文学之门
INSERT INTO soul_gateways (
    id,
    name,
    description,
    color_hex,
    icon,
    gateway_type,
    category,
    display_order,
    is_active,
    popularity_score,
    ai_prompt,
    target_dimensions,
    created_at,
    updated_at
) VALUES (
    'literature_gateway',
    '文学之门',
    '于纸墨留存的呼吸间\n触碰永恒的真理',
    '#3B82F6', -- blue
    'book-open',
    'literature',
    'knowledge',
    2,
    true,
    0,
    '根据用户选择的文学之门，生成5个关于阅读偏好的问题，涵盖文学类型、叙事风格、主题深度、语言美感和阅读场景等方面。',
    '{"narrative": 0.9, "intellectual": 0.8, "linguistic": 0.7, "philosophical": 0.9}',
    NOW(),
    NOW()
);

-- 插入音乐之门
INSERT INTO soul_gateways (
    id,
    name,
    description,
    color_hex,
    icon,
    gateway_type,
    category,
    display_order,
    is_active,
    popularity_score,
    ai_prompt,
    target_dimensions,
    created_at,
    updated_at
) VALUES (
    'music_gateway',
    '音乐之门',
    '由旋律激起的共振中\n释放压抑的灵魂',
    '#F59E0B', -- gold
    'music',
    'music',
    'art',
    3,
    true,
    0,
    '根据用户选择的音乐之门，生成5个关于音乐偏好的问题，涵盖音乐类型、情感共鸣、节奏偏好、聆听场景和音乐功能等方面。',
    '{"auditory": 0.9, "emotional": 0.8, "rhythmic": 0.7, "expressive": 0.8}',
    NOW(),
    NOW()
);

-- 插入游戏之门
INSERT INTO soul_gateways (
    id,
    name,
    description,
    color_hex,
    icon,
    gateway_type,
    category,
    display_order,
    is_active,
    popularity_score,
    ai_prompt,
    target_dimensions,
    created_at,
    updated_at
) VALUES (
    'game_gateway',
    '游戏之门',
    '在维度交错的交互里\n重塑世界的法则',
    '#10B981', -- green
    'gamepad-2',
    'game',
    'entertainment',
    4,
    true,
    0,
    '根据用户选择的游戏之门，生成5个关于游戏偏好的问题，涵盖游戏类型、交互方式、叙事深度、挑战难度和社交属性等方面。',
    '{"interactive": 0.9, "strategic": 0.7, "narrative": 0.6, "social": 0.5}',
    NOW(),
    NOW()
);

-- 验证插入结果
SELECT 
    id,
    name,
    gateway_type,
    color_hex,
    icon,
    is_active,
    display_order
FROM soul_gateways 
ORDER BY display_order;

-- 统计信息
SELECT 
    COUNT(*) as total_gateways,
    SUM(CASE WHEN is_active THEN 1 ELSE 0 END) as active_gateways,
    STRING_AGG(gateway_type, ', ') as gateway_types
FROM soul_gateways;