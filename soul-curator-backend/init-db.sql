-- SoulCurator 数据库初始化脚本
-- 版本：1.0
-- 日期：2026-04-13
-- 描述：完整的SoulCurator数据库表结构

-- 创建扩展（如果需要）
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- 设置搜索路径
SET search_path TO public;

-- ==================== 核心业务表 ====================

-- 1. 用户表（支持匿名和注册用户）
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    -- 用户标识
    session_id VARCHAR(64) UNIQUE NOT NULL,  -- 匿名用户的会话ID
    username VARCHAR(50) UNIQUE,             -- 用户名（注册用户）
    email VARCHAR(100) UNIQUE,               -- 邮箱（注册用户）
    avatar_url VARCHAR(500),                 -- 头像URL
    
    -- 用户状态
    is_anonymous BOOLEAN DEFAULT TRUE,       -- 是否为匿名用户
    is_active BOOLEAN DEFAULT TRUE,          -- 用户是否活跃
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    last_active_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 索引
    INDEX idx_users_session_id (session_id),
    INDEX idx_users_created_at (created_at)
);

COMMENT ON TABLE users IS '用户表，支持匿名和注册用户';
COMMENT ON COLUMN users.session_id IS '匿名用户的会话ID，用于关联未登录用户的数据';
COMMENT ON COLUMN users.is_anonymous IS '是否为匿名用户（true=匿名，false=注册）';

-- 2. 问题表（静态数据）
CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    -- 问题信息
    step_number INTEGER NOT NULL,            -- 步骤编号（1-5）
    title VARCHAR(200) NOT NULL,             -- 问题标题
    subtitle VARCHAR(500),                   -- 问题副标题
    description TEXT,                        -- 问题详细描述
    
    -- 灵魂之门关联
    gateway_type VARCHAR(20) NOT NULL DEFAULT 'movie',  -- 关联的灵魂之门入口类型
    
    -- 维度映射（每个问题对应哪些灵魂维度）
    dimension_mapping JSONB,                 -- JSON格式：{"visual": 2, "rational": 1, ...}
    
    -- AI扩展字段
    dimension_id UUID,                       -- 关联灵魂维度，用于AI生成个性化问题
    ai_prompt TEXT,                          -- AI生成提示词，用于指导AI生成问题内容
    
    -- 元数据
    is_active BOOLEAN DEFAULT TRUE,          -- 是否启用
    display_order INTEGER DEFAULT 0,         -- 显示顺序
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 约束
    UNIQUE(step_number, gateway_type),      -- 同一入口类型下步骤编号唯一
    CHECK (step_number BETWEEN 1 AND 20),    -- 预留扩展空间
    
    -- 索引
    INDEX idx_questions_step (step_number),
    INDEX idx_questions_gateway (gateway_type),
    INDEX idx_questions_active (is_active)
);

COMMENT ON TABLE questions IS '问题表，存储灵魂探索的各个问题';
COMMENT ON COLUMN questions.dimension_mapping IS '问题对应的灵魂维度权重映射';

-- 3. 选项表（静态数据）
CREATE TABLE options (
    id VARCHAR(50) PRIMARY KEY,              -- 选项ID（如"deep-reflection"）
    -- 关联信息
    question_id BIGINT NOT NULL REFERENCES questions(id) ON DELETE CASCADE,
    
    -- 选项内容
    title VARCHAR(100) NOT NULL,             -- 选项标题
    emoji VARCHAR(10),                       -- 表情符号
    description TEXT NOT NULL,               -- 选项描述
    references TEXT[],                       -- 参考作品数组
    
    -- 维度得分（选择此选项对各个维度的贡献）
    dimension_scores JSONB NOT NULL,         -- JSON格式：{"visual": 3, "rational": 2, ...}
    
    -- AI扩展字段
    weight JSONB,                            -- 权重配置，用于分支计算和AI路径选择
    next_question_id BIGINT,                 -- 固定流程的下一个问题，支持线性流程控制
    ai_context TEXT,                         -- AI生成上下文，提供选项生成的背景信息
    
    -- 元数据
    display_order INTEGER DEFAULT 0,         -- 显示顺序
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 索引
    INDEX idx_options_question (question_id),
    INDEX idx_options_order (question_id, display_order)
);

COMMENT ON TABLE options IS '选项表，每个问题的可选答案';
COMMENT ON COLUMN options.dimension_scores IS '选择此选项对各个灵魂维度的得分贡献';

-- 4. 灵魂维度定义表（静态数据）
CREATE TABLE soul_dimensions (
    id VARCHAR(50) PRIMARY KEY,              -- 维度ID（如"visual", "rational"）
    name VARCHAR(50) NOT NULL,               -- 维度名称（中文）
    english_name VARCHAR(50) NOT NULL,       -- 维度英文名称
    description TEXT NOT NULL,               -- 维度描述
    icon VARCHAR(50),                        -- 图标名称
    color_hex VARCHAR(7),                    -- 颜色代码
    
    -- 分组信息
    category VARCHAR(50),                    -- 分类：perception/thinking/emotion/social
    display_order INTEGER DEFAULT 0,         -- 显示顺序
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 索引
    INDEX idx_dimensions_category (category)
);

COMMENT ON TABLE soul_dimensions IS '灵魂维度定义表，定义16个灵魂维度的属性';

-- 5. 用户选择记录表
CREATE TABLE user_selections (
    id BIGSERIAL PRIMARY KEY,
    -- 关联信息
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id VARCHAR(64) NOT NULL,         -- 冗余存储，便于查询
    gateway_type VARCHAR(20) NOT NULL,       -- 灵魂之门入口类型
    
    -- 选择信息
    question_id BIGINT NOT NULL REFERENCES questions(id),
    option_id VARCHAR(50) NOT NULL REFERENCES options(id),
    
    -- 选择上下文
    step_number INTEGER NOT NULL,            -- 步骤编号
    time_spent_seconds INTEGER,              -- 花费时间（秒）
    
    -- AI扩展字段
    metadata JSONB,                          -- 扩展字段，存储用户选择的额外信息
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 约束
    UNIQUE(user_id, question_id),           -- 每个用户每个问题只能选一次
    
    -- 索引
    INDEX idx_selections_user (user_id),
    INDEX idx_selections_session (session_id),
    INDEX idx_selections_gateway (gateway_type),
    INDEX idx_selections_created (created_at)
);

COMMENT ON TABLE user_selections IS '用户选择记录表，记录用户在每个问题上的选择';

-- 6. 灵魂画像表（用户完成探索后生成）
CREATE TABLE soul_portraits (
    id BIGSERIAL PRIMARY KEY,
    -- 关联信息
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id VARCHAR(64) NOT NULL,
    
    -- 画像信息
    soul_type VARCHAR(100) NOT NULL,         -- 灵魂类型（如"科幻理性主义者"）
    description TEXT NOT NULL,               -- 灵魂描述
    quote_text TEXT,                         -- 引用文本
    quote_author VARCHAR(100),               -- 引用作者
    
    -- 维度得分
    dimension_scores JSONB NOT NULL,         -- 各维度最终得分
    top_dimensions VARCHAR(50)[],           -- 得分最高的3个维度
    
    -- 生成信息
    total_questions INTEGER DEFAULT 5,       -- 总问题数
    completed_questions INTEGER DEFAULT 5,   -- 完成问题数
    total_time_seconds INTEGER,              -- 总耗时（秒）
    
    -- 时间戳
    generated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 索引
    INDEX idx_portraits_user (user_id),
    INDEX idx_portraits_session (session_id),
    INDEX idx_portraits_generated (generated_at),
    INDEX idx_portraits_soul_type (soul_type)
);

COMMENT ON TABLE soul_portraits IS '灵魂画像表，用户完成探索后生成的灵魂画像';

-- 7. 推荐作品表（静态数据）
CREATE TABLE recommended_works (
    id BIGSERIAL PRIMARY KEY,
    -- 作品信息
    title VARCHAR(200) NOT NULL,             -- 作品标题
    original_title VARCHAR(200),             -- 原标题（外文）
    type VARCHAR(20) NOT NULL,               -- 类型：movie/book/music/game
    description TEXT NOT NULL,               -- 作品描述
    year INTEGER,                            -- 发布年份
    country VARCHAR(50),                     -- 国家/地区
    language VARCHAR(50),                    -- 语言
    
    -- 媒体信息
    cover_image_url VARCHAR(500),            -- 封面图URL
    trailer_url VARCHAR(500),                -- 预告片URL
    external_link VARCHAR(500),              -- 外部链接（豆瓣/IMDb等）
    
    -- 标签和分类
    tags VARCHAR(50)[],                      -- 标签数组
    genres VARCHAR(50)[],                    -- 类型数组
    
    -- 灵魂维度匹配
    dimension_mapping JSONB,                 -- 作品对应的灵魂维度
    
    -- 元数据
    is_active BOOLEAN DEFAULT TRUE,
    popularity_score INTEGER DEFAULT 0,      -- 热度评分
    quality_score INTEGER DEFAULT 0,         -- 质量评分
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 索引
    INDEX idx_works_type (type),
    INDEX idx_works_tags (tags),
    INDEX idx_works_popularity (popularity_score DESC)
);

COMMENT ON TABLE recommended_works IS '推荐作品表，存储可推荐的作品信息';

-- 8. 个性化推荐表
CREATE TABLE personalized_recommendations (
    id BIGSERIAL PRIMARY KEY,
    -- 关联信息
    portrait_id BIGINT NOT NULL REFERENCES soul_portraits(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    -- 推荐信息
    work_id BIGINT NOT NULL REFERENCES recommended_works(id),
    recommendation_reason TEXT NOT NULL,      -- 推荐理由
    match_score DECIMAL(5,2),                -- 匹配度分数（0-100）
    
    -- 用户反馈
    is_viewed BOOLEAN DEFAULT FALSE,         -- 用户是否查看
    is_saved BOOLEAN DEFAULT FALSE,          -- 用户是否收藏
    feedback_rating INTEGER,                 -- 用户评分（1-5）
    feedback_comment TEXT,                   -- 用户评论
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 约束
    UNIQUE(portrait_id, work_id),           -- 每个画像每个作品只推荐一次
    
    -- 索引
    INDEX idx_recommendations_portrait (portrait_id),
    INDEX idx_recommendations_user (user_id),
    INDEX idx_recommendations_match_score (match_score DESC)
);

COMMENT ON TABLE personalized_recommendations IS '个性化推荐表，为用户生成的个性化推荐';

-- ==================== 埋点与分析表 ====================

-- 9. 用户事件埋点表
CREATE TABLE user_events (
    id BIGSERIAL PRIMARY KEY,
    -- 用户标识
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    session_id VARCHAR(64) NOT NULL,
    
    -- 事件信息
    event_type VARCHAR(50) NOT NULL,         -- 事件类型：page_view/selection/portrait_generated
    event_name VARCHAR(100) NOT NULL,        -- 事件名称
    page_url VARCHAR(500),                   -- 页面URL
    event_data JSONB,                        -- 事件详细数据
    
    -- 设备信息
    user_agent TEXT,                         -- 浏览器User-Agent
    ip_address INET,                         -- IP地址（匿名化处理）
    screen_resolution VARCHAR(50),           -- 屏幕分辨率
    device_type VARCHAR(50),                 -- 设备类型：desktop/mobile/tablet
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 索引
    INDEX idx_events_session (session_id),
    INDEX idx_events_type (event_type),
    INDEX idx_events_created (created_at),
    INDEX idx_events_user (user_id)
);

COMMENT ON TABLE user_events IS '用户事件埋点表，记录用户行为数据';

-- 10. 会话分析表
CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,
    -- 会话信息
    session_id VARCHAR(64) UNIQUE NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE SET NULL,
    
    -- 会话统计
    total_events INTEGER DEFAULT 0,          -- 总事件数
    total_questions INTEGER DEFAULT 0,       -- 回答问题数
    total_time_seconds INTEGER DEFAULT 0,    -- 总会话时长
    
    -- 会话状态
    is_completed BOOLEAN DEFAULT FALSE,      -- 是否完成灵魂探索
    portrait_id BIGINT REFERENCES soul_portraits(id), -- 生成的画像ID
    
    -- 设备信息
    first_user_agent TEXT,                   -- 首次User-Agent
    first_ip_address INET,                   -- 首次IP
    first_screen_resolution VARCHAR(50),     -- 首次屏幕分辨率
    
    -- 时间范围
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ended_at TIMESTAMP WITH TIME ZONE,
    last_active_at TIMESTAMP WITH TIME ZONE,
    
    -- 索引
    INDEX idx_sessions_user (user_id),
    INDEX idx_sessions_started (started_at),
    INDEX idx_sessions_completed (is_completed)
);

COMMENT ON TABLE user_sessions IS '用户会话分析表，记录用户会话的完整信息';

-- ==================== 系统管理表 ====================

-- 11. 系统配置表
CREATE TABLE system_configs (
    id VARCHAR(50) PRIMARY KEY,              -- 配置键
    value JSONB NOT NULL,                    -- 配置值（JSON格式）
    description TEXT,                        -- 配置描述
    is_public BOOLEAN DEFAULT FALSE,         -- 是否公开配置
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 索引
    INDEX idx_configs_public (is_public)
);

COMMENT ON TABLE system_configs IS '系统配置表，存储系统运行时配置';

-- ==================== 初始化数据 ====================

-- 插入默认的灵魂维度定义（基于PRD中的16个维度）
INSERT INTO soul_dimensions (id, name, english_name, description, icon, color_hex, category, display_order) VALUES
-- 感知维度
('visual', '视觉性', 'Visual', '对视觉艺术、画面构图、色彩美学的敏感度和偏好', 'eye', '#3B82F6', 'perception', 1),
('auditory', '听觉性', 'Auditory', '对音乐、声音设计、语言韵律的敏感度和偏好', 'music', '#10B981', 'perception', 2),
('narrative', '叙事性', 'Narrative', '对故事结构、情节发展、人物弧光的关注程度', 'book-open', '#8B5CF6', 'perception', 3),
('interactive', '互动性', 'Interactive', '对参与感、选择权、交互体验的重视程度', 'gamepad-2', '#EF4444', 'perception', 4),

-- 思维维度
('emotional', '情感强度', 'Emotional Intensity', '情感表达的深度、强度和直接程度', 'heart', '#EC4899', 'thinking', 5),
('rational', '理性程度', 'Rationality', '逻辑分析、客观思考、系统思维的倾向', 'brain', '#6366F1', 'thinking', 6),
('traditional', '传统倾向', 'Traditional', '对经典形式、既定规则、保守价值的认同', 'shield', '#F59E0B', 'thinking', 7),
('innovative', '创新倾向', 'Innovative', '对新颖形式、突破规则、前卫价值的追求', 'zap', '#06B6D4', 'thinking', 8),

-- 社会维度
('individual', '个人焦点', 'Individual Focus', '关注个体命运、内心世界、自我实现的倾向', 'user', '#8B5CF6', 'social', 9),
('collective', '集体焦点', 'Collective Focus', '关注群体命运、社会关系、共同价值的倾向', 'users', '#10B981', 'social', 10),

-- 秩序维度
('order', '秩序需求', 'Order', '对结构清晰、规则明确、可预测性的需求', 'layout-grid', '#3B82F6', 'order', 11),
('chaos', '混乱容忍', 'Chaos Tolerance', '对模糊边界、随机性、不确定性的接受程度', 'wind', '#F59E0B', 'order', 12),

-- 现实维度
('realistic', '现实锚定', 'Realistic', '对真实世界、现实逻辑、日常经验的重视', 'anchor', '#6366F1', 'reality', 13),
('fantasy', '幻想倾向', 'Fantasy', '对想象世界、超现实逻辑、奇幻经验的偏好', 'sparkles', '#EC4899', 'reality', 14),

-- 表达维度
('introspective', '内向探索', 'Introspective', '倾向于内省、沉思、自我反思的表达