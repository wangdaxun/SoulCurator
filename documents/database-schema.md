# SoulCurator 数据库架构文档

## 数据库概览
- **数据库名称**: soulcurator_db
- **表数量**: 11张表
- **设计模式**: 星型架构 + 维度建模
- **更新日期**: 2026-04-13

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
    
    -- 配置关系（独立）
    system_configs ||--|| system_configs : "配置"
    
    -- 维度关系（弱关系）
    soul_dimensions }|..|| questions : "映射"
    soul_dimensions }|..|| options : "贡献"
    soul_dimensions }|..|| soul_portraits : "得分"
    soul_dimensions }|..|| recommended_works : "匹配"
```

## 表详细说明

### 1. 核心业务表

#### 1.1 用户表 (users)
**作用**: 存储用户信息，支持匿名和注册用户
```sql
users {
    id BIGSERIAL PK
    session_id VARCHAR(64) UNIQUE
    username VARCHAR(50) UNIQUE
    email VARCHAR(100) UNIQUE
    avatar_url VARCHAR(500)
    is_anonymous BOOLEAN DEFAULT TRUE
    is_active BOOLEAN DEFAULT TRUE
    created_at TIMESTAMPTZ
    updated_at TIMESTAMPTZ
    last_active_at TIMESTAMPTZ
}
```

#### 1.2 问题表 (questions)
**作用**: 存储灵魂探索的5个问题
```sql
questions {
    id BIGSERIAL PK
    step_number INTEGER UNIQUE
    title VARCHAR(200)
    subtitle VARCHAR(500)
    description TEXT
    dimension_mapping JSONB
    is_active BOOLEAN DEFAULT TRUE
    display_order INTEGER
    created_at TIMESTAMPTZ
    updated_at TIMESTAMPTZ
}
```

#### 1.3 选项表 (options)
**作用**: 每个问题的4个可选答案
```sql
options {
    id VARCHAR(50) PK
    question_id BIGINT FK -> questions.id
    title VARCHAR(100)
    emoji VARCHAR(10)
    description TEXT
    work_references TEXT[]
    dimension_scores JSONB
    display_order INTEGER
    created_at TIMESTAMPTZ
    updated_at TIMESTAMPTZ
}
```

#### 1.4 用户选择记录表 (user_selections)
**作用**: 记录用户在每一步的选择
```sql
user_selections {
    id BIGSERIAL PK
    user_id BIGINT FK -> users.id
    session_id VARCHAR(64)
    question_id BIGINT FK -> questions.id
    option_id VARCHAR(50) FK -> options.id
    step_number INTEGER
    time_spent_seconds INTEGER
    created_at TIMESTAMPTZ
}
```

#### 1.5 灵魂画像表 (soul_portraits)
**作用**: 用户完成探索后生成的灵魂画像
```sql
soul_portraits {
    id BIGSERIAL PK
    user_id BIGINT FK -> users.id
    session_id VARCHAR(64)
    soul_type VARCHAR(100)
    description TEXT
    quote_text TEXT
    quote_author VARCHAR(100)
    dimension_scores JSONB
    top_dimensions VARCHAR(50)[]
    total_questions INTEGER
    completed_questions INTEGER
    total_time_seconds INTEGER
    generated_at TIMESTAMPTZ
}
```

#### 1.6 推荐作品表 (recommended_works)
**作用**: 可推荐的作品库
```sql
recommended_works {
    id BIGSERIAL PK
    title VARCHAR(200)
    original_title VARCHAR(200)
    type VARCHAR(20)  -- movie/book/music/game
    description TEXT
    year INTEGER
    country VARCHAR(50)
    language VARCHAR(50)
    cover_image_url VARCHAR(500)
    trailer_url VARCHAR(500)
    external_link VARCHAR(500)
    tags VARCHAR(50)[]
    genres VARCHAR(50)[]
    dimension_mapping JSONB
    is_active BOOLEAN
    popularity_score INTEGER
    quality_score INTEGER
    created_at TIMESTAMPTZ
    updated_at TIMESTAMPTZ
}
```

#### 1.7 个性化推荐表 (personalized_recommendations)
**作用**: 为用户生成的个性化推荐
```sql
personalized_recommendations {
    id BIGSERIAL PK
    portrait_id BIGINT FK -> soul_portraits.id
    user_id BIGINT FK -> users.id
    work_id BIGINT FK -> recommended_works.id
    recommendation_reason TEXT
    match_score DECIMAL(5,2)
    is_viewed BOOLEAN
    is_saved BOOLEAN
    feedback_rating INTEGER
    feedback_comment TEXT
    created_at TIMESTAMPTZ
    updated_at TIMESTAMPTZ
}
```

### 2. 分析表

#### 2.1 用户事件埋点表 (user_events)
**作用**: 记录用户行为数据
```sql
user_events {
    id BIGSERIAL PK
    user_id BIGINT FK -> users.id
    session_id VARCHAR(64)
    event_type VARCHAR(50)  -- page_view/selection/portrait_generated
    event_name VARCHAR(100)
    page_url VARCHAR(500)
    event_data JSONB
    user_agent TEXT
    ip_address INET
    screen_resolution VARCHAR(50)
    device_type VARCHAR(50)
    created_at TIMESTAMPTZ
}
```

#### 2.2 会话分析表 (user_sessions)
**作用**: 记录用户会话的完整信息
```sql
user_sessions {
    id BIGSERIAL PK
    session_id VARCHAR(64) UNIQUE
    user_id BIGINT FK -> users.id
    total_events INTEGER
    total_questions INTEGER
    total_time_seconds INTEGER
    is_completed BOOLEAN
    portrait_id BIGINT FK -> soul_portraits.id
    first_user_agent TEXT
    first_ip_address INET
    first_screen_resolution VARCHAR(50)
    started_at TIMESTAMPTZ
    ended_at TIMESTAMPTZ
    last_active_at TIMESTAMPTZ
}
```

### 3. 配置表

#### 3.1 系统配置表 (system_configs)
**作用**: 存储系统运行时配置
```sql
system_configs {
    id VARCHAR(50) PK
    value JSONB
    description TEXT
    is_public BOOLEAN
    created_at TIMESTAMPTZ
    updated_at TIMESTAMPTZ
}
```

### 4. 维度表

#### 4.1 灵魂维度定义表 (soul_dimensions)
**作用**: 定义16个灵魂维度的属性
```sql
soul_dimensions {
    id VARCHAR(50) PK  -- visual/auditory/narrative/...
    name VARCHAR(50)
    english_name VARCHAR(50)
    description TEXT
    icon VARCHAR(50)
    color_hex VARCHAR(7)
    category VARCHAR(50)  -- perception/thinking/emotion/social
    display_order INTEGER
    created_at TIMESTAMPTZ
}
```

## 数据流分析

### 1. 核心业务流程
```
用户访问 → 创建会话 → 回答问题 → 生成画像 → 获得推荐
    ↓          ↓           ↓          ↓           ↓
user_sessions user_events user_selections soul_portraits personalized_recommendations
    ↑          ↑           ↑          ↑           ↑
    users      users       users      users       users
              questions   options              recommended_works
```

### 2. 分析流程
```
用户行为 → 事件埋点 → 会话统计 → 数据分析
    ↓          ↓           ↓          ↓
user_events user_sessions 报表系统 业务洞察
    ↑          ↑
    users      users
              soul_portraits
```

### 3. 维度计算流程
```
用户选择 → 维度得分 → 灵魂画像 → 作品匹配
    ↓          ↓           ↓          ↓
options.dimension_scores soul_portraits.dimension_scores recommended_works.dimension_mapping
    ↑          ↑           ↑          ↑
soul_dimensions soul_dimensions soul_dimensions soul_dimensions
```

### 4. 完整关系链
```
1. 用户注册/匿名访问
   users ←→ user_sessions
   
2. 用户进行灵魂探索
   users → user_selections ← questions ← options
   
3. 生成灵魂画像
   users → soul_portraits ← user_selections
   
4. 获得个性化推荐
   soul_portraits → personalized_recommendations ← recommended_works
   
5. 行为记录与分析
   users → user_events → user_sessions
   
6. 维度计算贯穿始终
   soul_dimensions → options → user_selections → soul_portraits → personalized_recommendations
```

## 索引策略

### 1. 查询优化索引
- **用户相关查询**: `idx_users_session_id`, `idx_users_created_at`
- **选择记录查询**: `idx_selections_user`, `idx_selections_session`
- **画像查询**: `idx_portraits_user`, `idx_portraits_generated`
- **推荐查询**: `idx_recommendations_portrait`, `idx_recommendations_match_score`

### 2. 分析优化索引
- **事件分析**: `idx_events_session_created`, `idx_events_type`
- **会话分析**: `idx_sessions_user_started`, `idx_sessions_completed`

### 3. 条件索引（部分索引）
- 活跃用户: `idx_users_is_active`
- 活跃作品: `idx_works_active`
- 已查看推荐: `idx_recommendations_viewed`

## 数据量预估

### 1. 核心业务表
- **users**: 预计10万用户（90%匿名）
- **user_selections**: 50万条记录（每个用户平均5次选择）
- **soul_portraits**: 10万条记录（每个用户一个画像）
- **personalized_recommendations**: 30万条记录（每个画像3个推荐）

### 2. 分析表
- **user_events**: 预计1000万条事件（每个用户平均100个事件）
- **user_sessions**: 20万条会话（每个用户平均2次会话）

### 3. 静态表
- **questions**: 5条记录（固定）
- **options**: 20条记录（5个问题×4个选项）
- **soul_dimensions**: 16条记录（固定）
- **recommended_works**: 预计1000条作品

## 更新维护

### 1. 表结构变更
当需要修改表结构时：
1. 创建迁移脚本
2. 备份数据
3. 执行迁移
4. 更新本文档

### 2. 数据维护
- **用户数据**: 保留2年，匿名用户数据90天后清理
- **事件数据**: 保留1年，按月分区
- **会话数据**: 保留6个月

### 3. 性能监控
- 监控表大小增长
- 监控索引使用情况
- 定期分析查询性能

---

**文档版本**: v1.0  
**最后更新**: 2026-04-13  
**维护者**: 王达迅  
**下次评审**: 2026-05-13