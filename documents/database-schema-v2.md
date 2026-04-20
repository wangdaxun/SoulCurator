# SoulCurator 数据库架构文档 v2.0

## 数据库概览
- **数据库名称**: soulcurator_db
- **表数量**: 15张表（包含新增的soul_gateways、soul_prototypes、description_variables表）
- **设计模式**: 星型架构 + 维度建模 + 规则引擎配置
- **更新日期**: 2026-04-20
- **主要更新**: 灵魂画像生成功能扩展

## 新增表说明

### 1. 灵魂之门入口表 (soul_gateways)
为了支持方案B（后端统一维护入口配置），新增soul_gateways表：

```sql
soul_gateways {
    VARCHAR(50) id PK                    -- 入口类型ID，如'movie', 'literature'
    VARCHAR(100) name                    -- 入口名称，如'电影之门'
    TEXT description                     -- 入口描述
    VARCHAR(7) color_hex                -- 颜色代码，如'#8B5CF6'
    VARCHAR(50) icon                    -- 图标名称，如'clapperboard'
    VARCHAR(50) gateway_type            -- 入口类型：movie/literature/music/game
    VARCHAR(50) category                -- 分类：art/entertainment/knowledge
    INTEGER display_order               -- 显示顺序
    BOOLEAN is_active                   -- 是否启用
    INTEGER popularity_score            -- 热度评分
    DECIMAL(5,2) completion_rate        -- 完成率
    TEXT ai_prompt                      -- AI生成提示词
    JSONB target_dimensions             -- 目标维度
    TIMESTAMPTZ created_at
    TIMESTAMPTZ updated_at
}
```

### 2. 灵魂原型配置表 (soul_prototypes)
为了支持规则引擎生成灵魂画像，新增soul_prototypes表：

```sql
soul_prototypes {
    BIGSERIAL id PK                     -- 主键
    VARCHAR(50) name UK                 -- 原型名称，如'深度思考者'
    TEXT description_template           -- 描述模板，支持变量替换
    VARCHAR(50) icon                    -- 默认图标
    VARCHAR(7) color_hex               -- 默认颜色，如'#8b5cf6'
    JSONB dimension_weights            -- 维度权重配置，如{"introspection": 8, "logic": 6}
    JSONB traits                       -- 特质列表，JSON数组格式
    JSONB quotes                       -- 引用列表，JSON数组格式
    BOOLEAN is_active                  -- 是否启用
    INTEGER display_order              -- 显示顺序
    TIMESTAMPTZ created_at
    TIMESTAMPTZ updated_at
}
```

### 3. 描述模板变量表 (description_variables)
为了支持动态描述生成，新增description_variables表：

```sql
description_variables {
    BIGSERIAL id PK                     -- 主键
    VARCHAR(50) variable_key           -- 变量键，如'${adjective}'
    VARCHAR(20) variable_type          -- 变量类型：adjective/time/theme
    JSONB values                       -- 变量值数组，JSON格式
    VARCHAR(50) dimension              -- 关联的维度（可选）
    BOOLEAN is_active                  -- 是否启用
    TIMESTAMPTZ created_at
    TIMESTAMPTZ updated_at
    UNIQUE(variable_key, variable_type) -- 唯一约束
}
```

### questions表新增字段
- **gateway_type (VARCHAR(50))**: 关联的灵魂之门入口类型

## 扩展字段说明

### 1. AI能力扩展字段
为了支持未来的AI能力扩展，以下表添加了预留字段：

#### questions表新增字段
- **dimension_id (UUID)**: 关联灵魂维度，用于AI生成个性化问题
- **ai_prompt (TEXT)**: AI生成提示词，用于指导AI生成问题内容

#### options表新增字段
- **weight (JSONB)**: 权重配置，用于分支计算和AI路径选择
- **next_question_id (UUID)**: 固定流程的下一个问题，支持线性流程控制
- **ai_context (TEXT)**: AI生成上下文，提供选项生成的背景信息

#### user_selections表新增字段
- **metadata (JSONB)**: 扩展字段，存储用户选择的额外信息，如AI分析结果、情感数据等

### 2. 规则引擎扩展字段
为了支持规则引擎生成灵魂画像，以下表添加了扩展字段：

#### soul_portraits表新增字段
- **traits (JSONB)**: 特质列表，格式：[{"name": "深度内省", "icon": "moon", "color": "#8b5cf6"}]
- **quote (JSONB)**: 引用信息，格式：{"text": "不要温顺地走进那个良夜", "author": "狄兰·托马斯"}
- **metadata (JSONB)**: 元数据，格式：{"generatedAt": "2026-04-20T18:20:00+08:00", "soulId": "0x7E...42C", "node": "深空节点"}
- **generation_method (VARCHAR(20))**: 生成方法：rule_engine（规则引擎）/ ai（AI生成）
- **match_score (DECIMAL(5,2))**: 匹配度分数，表示用户选择与画像原型的匹配程度

#### recommended_works表新增字段
- **soul_types (VARCHAR(50)[])**: 匹配的灵魂类型数组，如：["深度思考者", "感性体验者"]
- **icon (VARCHAR(50))**: 图标名称，前端使用的图标标识
- **color_hex (VARCHAR(7))**: 颜色代码，十六进制格式，如：#8b5cf6
- **display_order (INTEGER)**: 显示顺序，数值越小越靠前

## 表关系图

```mermaid
erDiagram
    users {
        BIGSERIAL id PK
        VARCHAR(64) session_id UK
        VARCHAR(50) username UK
        VARCHAR(100) email UK
        VARCHAR(500) avatar_url
        BOOLEAN is_anonymous
        BOOLEAN is_active
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
        TIMESTAMPTZ last_active_at
    }
    
    questions {
        BIGSERIAL id PK
        INTEGER step_number UK
        VARCHAR(200) title
        VARCHAR(500) subtitle
        TEXT description
        JSONB dimension_mapping
        BOOLEAN is_active
        INTEGER display_order
        UUID dimension_id
        TEXT ai_prompt
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }
    
    options {
        VARCHAR(50) id PK
        BIGINT question_id FK
        VARCHAR(100) title
        VARCHAR(10) emoji
        TEXT description
        TEXT[] work_references
        JSONB dimension_scores
        INTEGER display_order
        JSONB weight
        UUID next_question_id
        TEXT ai_context
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }
    
    user_selections {
        BIGSERIAL id PK
        BIGINT user_id FK
        VARCHAR(64) session_id
        BIGINT question_id FK
        VARCHAR(50) option_id FK
        INTEGER step_number
        INTEGER time_spent_seconds
        JSONB metadata
        TIMESTAMPTZ created_at
    }
    
    soul_portraits {
        BIGSERIAL id PK
        BIGINT user_id FK
        VARCHAR(64) session_id
        VARCHAR(100) soul_type
        TEXT description
        TEXT quote_text
        VARCHAR(100) quote_author
        JSONB dimension_scores
        VARCHAR(50)[] top_dimensions
        INTEGER total_questions
        INTEGER completed_questions
        INTEGER total_time_seconds
        TIMESTAMPTZ generated_at
        JSONB traits
        JSONB quote
        JSONB metadata
        VARCHAR(20) generation_method
        DECIMAL(5,2) match_score
    }
    
    recommended_works {
        BIGSERIAL id PK
        VARCHAR(200) title
        VARCHAR(200) original_title
        VARCHAR(20) type
        TEXT description
        INTEGER year
        VARCHAR(50) country
        VARCHAR(50) language
        VARCHAR(500) cover_image_url
        VARCHAR(500) trailer_url
        VARCHAR(500) external_link
        VARCHAR(50)[] tags
        VARCHAR(50)[] genres
        JSONB dimension_mapping
        BOOLEAN is_active
        INTEGER popularity_score
        INTEGER quality_score
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
        VARCHAR(50)[] soul_types
        VARCHAR(50) icon
        VARCHAR(7) color_hex
        INTEGER display_order
    }
    
    personalized_recommendations {
        BIGSERIAL id PK
        BIGINT portrait_id FK
        BIGINT user_id FK
        BIGINT work_id FK
        TEXT recommendation_reason
        DECIMAL(5,2) match_score
        BOOLEAN is_viewed
        BOOLEAN is_saved
        INTEGER feedback_rating
        TEXT feedback_comment
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }
    
    user_events {
        BIGSERIAL id PK
        BIGINT user_id FK
        VARCHAR(64) session_id
        VARCHAR(50) event_type
        VARCHAR(100) event_name
        VARCHAR(500) page_url
        JSONB event_data
        TEXT user_agent
        INET ip_address
        VARCHAR(50) screen_resolution
        VARCHAR(50) device_type
        TIMESTAMPTZ created_at
    }
    
    user_sessions {
        BIGSERIAL id PK
        VARCHAR(64) session_id UK
        BIGINT user_id FK
        INTEGER total_events
        INTEGER total_questions
        INTEGER total_time_seconds
        BOOLEAN is_completed
        BIGINT portrait_id FK
        TEXT first_user_agent
        INET first_ip_address
        VARCHAR(50) first_screen_resolution
        TIMESTAMPTZ started_at
        TIMESTAMPTZ ended_at
        TIMESTAMPTZ last_active_at
    }
    
    system_configs {
        VARCHAR(50) id PK
        JSONB value
        TEXT description
        BOOLEAN is_public
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }
    
    soul_dimensions {
        VARCHAR(50) id PK
        VARCHAR(50) name
        VARCHAR(50) english_name
        TEXT description
        VARCHAR(50) icon
        VARCHAR(7) color_hex
        VARCHAR(50) category
        INTEGER display_order
        TIMESTAMPTZ created_at
    }
    
    soul_gateways {
        VARCHAR(50) id PK
        VARCHAR(100) name
        TEXT description
        VARCHAR(7) color_hex
        VARCHAR(50) icon
        VARCHAR(50) gateway_type
        VARCHAR(50) category
        INTEGER display_order
        BOOLEAN is_active
        INTEGER popularity_score
        DECIMAL(5,2) completion_rate
        TEXT ai_prompt
        JSONB target_dimensions
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }
    
    soul_prototypes {
        BIGSERIAL id PK
        VARCHAR(50) name UK
        TEXT description_template
        VARCHAR(50) icon
        VARCHAR(7) color_hex
        JSONB dimension_weights
        JSONB traits
        JSONB quotes
        BOOLEAN is_active
        INTEGER display_order
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }
    
    description_variables {
        BIGSERIAL id PK
        VARCHAR(50) variable_key
        VARCHAR(20) variable_type
        JSONB values
        VARCHAR(50) dimension
        BOOLEAN is_active
        TIMESTAMPTZ created_at
        TIMESTAMPTZ updated_at
    }
    
    -- 核心业务关系
    users ||--o{ user_selections : "记录"
    users ||--o{ soul_portraits : "生成"
    users ||--o{ user_events : "触发"
    users ||--|| user_sessions : "拥有"
    
    questions ||--o{ options : "包含"
    questions ||--o{ user_selections : "被回答"
    
    options ||--o{ user_selections : "被选择"
    
    soul_portraits ||--o{ personalized_recommendations : "产生"
    soul_portraits ||--|| user_sessions : "完成"
    
    recommended_works ||--o{ personalized_recommendations : "被推荐"
    
    -- 分析关系
    user_events }o--|| user_sessions : "属于"
    
    -- 配置关系
    system_configs ||--|| system_configs : "配置"
    
    -- 维度关系
    soul_dimensions }|..|| questions : "映射"
    soul_dimensions }|..|| options : "贡献"
    soul_dimensions }|..|| soul_portraits : "得分"
    soul_dimensions }|..|| recommended_works : "匹配"
    
    -- 灵魂之门关系
    soul_gateways }|..|| questions : "关联"
    
    -- 规则引擎关系
    soul_prototypes }|..|| soul_portraits : "匹配"
    soul_prototypes }|..|| recommended_works : "推荐"
    description_variables }|..|| soul_prototypes : "变量"
```

## 表详细说明

### 1. 核心业务表

#### 1.1 用户表 (users)
**作用**: 存储用户信息，支持匿名和注册用户

#### 1.2 问题表 (questions)
**作用**: 存储灵魂探索的5个问题

#### 1.3 选项表 (options)
**作用**: 每个问题的4个可选答案

#### 1.4 用户选择记录表 (user_selections)
**作用**: 记录用户在每一步的选择

#### 1.5 灵魂画像表 (soul_portraits) - **已扩展**
**作用**: 用户完成探索后生成的灵魂画像
**新增字段**:
- `traits`: 特质列表，JSONB格式
- `quote`: 引用信息，JSONB格式  
- `metadata`: 元数据，JSONB格式
- `generation_method`: 生成方法（rule_engine/ai）
- `match_score`: 匹配度分数

#### 1.6 推荐作品表 (recommended_works) - **已扩展**
**作用**: 可推荐的作品库
**新增字段**:
- `soul_types`: 匹配的灵魂类型数组
- `icon`: 图标名称
- `color_hex`: 颜色代码
- `display_order`: 显示顺序

#### 1.7 个性化推荐表 (personalized_recommendations)
**作用**: 为用户生成的个性化推荐

### 2. 分析表

#### 2.1 用户事件埋点表 (user_events)
**作用**: 记录用户行为数据

#### 2.2 会话分析表 (user_sessions)
**作用**: 记录用户会话的完整信息

### 3. 配置表

#### 3.1 系统配置表 (system_configs)
**作用**: 存储系统运行时配置

### 4. 维度表

#### 4.1 灵魂维度定义表 (soul_dimensions)
**作用**: 定义16个灵魂维度的属性

### 5. 新增配置表

#### 5.1 灵魂之门入口表 (soul_gateways)
**作用**: 存储灵魂之门入口的配置信息

#### 5.2 灵魂原型配置表 (soul_prototypes) - **新增**
**作用**: 存储规则引擎的配置，用于匹配用户选择生成灵魂画像
**关键字段**:
- `name`: 原型名称（如：深度思考者）
- `description_template`: 描述模板，支持变量替换
- `dimension_weights`: 维度权重配置
- `traits`: 特质列表
- `quotes`: 引用列表

#### 5.3 描述模板变量表 (description_variables) - **新增**
**作用**: 存储描述模板的变量，支持动态描述生成
**关键字段**:
- `variable_key`: 变量键（如：${adjective}）
- `variable_type`: 变量类型（adjective/time/theme）
- `values`: 变量值数组

## 规则引擎工作原理

### 1. 数据流
```
用户选择 → 维度权重计算 → 原型匹配 → 画像生成 → 作品推荐
    ↓          ↓           ↓          ↓           ↓
user_selections 规则引擎 soul_prototypes soul_portraits recommended_works
              description_variables
```

### 2. 原型匹配算法
1. **计算维度权重**: 分析用户选择，计算各维度得分
2. **匹配原型**: 根据维度权重匹配最接近的soul_prototypes
3. **生成描述**: 使用description_template和description_variables生成个性化描述
4. **选择特质**: 从prototype.traits中选择特质列表
5. **选择引用**: 从prototype.quotes中随机选择引用

### 3. 作品推荐逻辑
1. **类型匹配**: 根据soul_portraits.soul_type匹配recommended_works.soul_types
2. **排序**: 按display_order排序，优先显示高匹配度作品
3. **数量控制**: 每个画像推荐3个作品

## 数据流分析

### 1. 核心业务流程
```
用户访问 → 创建会话 → 回答问题 → 生成画像 → 获得推荐
    ↓          ↓           ↓          ↓           ↓
user_sessions user_events user_selections soul_portraits personalized_recommendations
    ↑          ↑           ↑          ↑           ↑
    users      users       users      users       users
              questions   options              recommended_works
                                             soul_prototypes
```

### 2. 规则引擎流程
```
用户选择 → 维度计算 → 原型匹配 → 变量替换 → 画像生成
    ↓          ↓