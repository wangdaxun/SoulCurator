    ↓          ↓           ↓          ↓           ↓
user_selections 规则引擎 soul_prototypes description_variables soul_portraits
```

### 3. 完整关系链
```
1. 用户注册/匿名访问
   users ←→ user_sessions
   
2. 用户进行灵魂探索
   users → user_selections ← questions ← options
   
3. 生成灵魂画像（规则引擎）
   users → soul_portraits ← user_selections
   soul_prototypes → soul_portraits（匹配原型）
   description_variables → soul_portraits（变量替换）
   
4. 获得个性化推荐
   soul_portraits → personalized_recommendations ← recommended_works
   soul_prototypes → recommended_works（类型匹配）
   
5. 行为记录与分析
   users → user_events → user_sessions
   
6. 维度计算贯穿始终
   soul_dimensions → options → user_selections → soul_portraits → personalized_recommendations
```

## 索引策略

### 1. 查询优化索引
- **用户相关查询**: `idx_users_session_id`, `idx_users_created_at`
- **选择记录查询**: `idx_selections_user`, `idx_selections_session`
- **画像查询**: `idx_portraits_user`, `idx_portraits_generated`, `idx_portraits_generation_method`
- **推荐查询**: `idx_recommendations_portrait`, `idx_recommendations_match_score`

### 2. 规则引擎优化索引
- **原型查询**: `idx_prototypes_active`, `idx_prototypes_order`, `idx_prototypes_dimension_weights` (GIN)
- **变量查询**: `idx_variables_type`, `idx_variables_dimension`
- **作品匹配**: `idx_works_soul_types` (GIN), `idx_works_display_order`

### 3. 分析优化索引
- **事件分析**: `idx_events_session_created`, `idx_events_type`
- **会话分析**: `idx_sessions_user_started`, `idx_sessions_completed`

## 数据量预估

### 1. 核心业务表
- **users**: 预计10万用户（90%匿名）
- **user_selections**: 50万条记录（每个用户平均5次选择）
- **soul_portraits**: 10万条记录（每个用户一个画像）
- **personalized_recommendations**: 30万条记录（每个画像3个推荐）

### 2. 规则引擎配置表
- **soul_prototypes**: 10-20条记录（预定义原型）
- **description_variables**: 10-30条记录（变量配置）
- **recommended_works**: 预计1000条作品（扩展后）

### 3. 分析表
- **user_events**: 预计1000万条事件（每个用户平均100个事件）
- **user_sessions**: 20万条会话（每个用户平均2次会话）

### 4. 静态表
- **questions**: 5条记录（固定）
- **options**: 20条记录（5个问题×4个选项）
- **soul_dimensions**: 16条记录（固定）
- **soul_gateways**: 4-8条记录（入口类型）

## 初始数据配置

### 1. 灵魂原型（预置）
```sql
-- 深度思考者
INSERT INTO soul_prototypes (name, description_template, dimension_weights, traits, quotes) VALUES
('深度思考者', '你的灵魂在${adjective}的星空下共鸣，于${time}之时寻找${theme}的逻辑与诗意。',
 '{"introspection": 8, "logic": 6, "art": 4, "adventure": 2}',
 '[{"name": "深度内省", "icon": "moon"}, {"name": "理性极客", "icon": "cpu"}]',
 '[{"text": "不要温顺地走进那个良夜", "author": "狄兰·托马斯"}]');

-- 感性体验者
INSERT INTO soul_prototypes (name, description_template, dimension_weights, traits, quotes) VALUES
('感性体验者', '你的心灵在${adjective}的色彩中舞蹈，于${time}的旋律里感受${theme}的温度与颤动。',
 '{"emotion": 8, "art": 7, "introspection": 5, "logic": 3}',
 '[{"name": "情感丰沛", "icon": "heart"}, {"name": "艺术感知", "icon": "music"}]',
 '[{"text": "心有猛虎，细嗅蔷薇", "author": "西格里夫·萨松"}]');

-- 冒险探索者
INSERT INTO soul_prototypes (name, description_template, dimension_weights, traits, quotes) VALUES
('冒险探索者', '你的精神在${adjective}的山巅翱翔，于${time}的未知中追寻${theme}的边界与可能。',
 '{"adventure": 9, "curiosity": 7, "logic": 5, "introspection": 3}',
 '[{"name": "无畏冒险", "icon": "mountain"}, {"name": "好奇求知", "icon": "globe"}]',
 '[{"text": "路漫漫其修远兮，吾将上下而求索", "author": "屈原"}]');
```

### 2. 描述变量（预置）
```sql
INSERT INTO description_variables (variable_key, variable_type, values) VALUES
('${adjective}', 'adjective', '["静谧的", "深邃的", "神秘的", "璀璨的"]'),
('${time}', 'time', '["万籁俱寂", "晨光熹微", "暮色苍茫", "午夜时分"]'),
('${theme}', 'theme', '["宇宙", "生命", "时间", "存在", "意识", "真理"]');
```

### 3. 推荐作品（示例）
```sql
INSERT INTO recommended_works (title, type, description, soul_types, icon, color_hex) VALUES
('《星际穿越》', 'movie', '唯有爱与时间能超越维度。', ARRAY['深度思考者', '冒险探索者'], 'rocket', '#8b5cf6'),
('Moonlight Sonata', 'music', '贝多芬的月光奏鸣曲。', ARRAY['深度思考者', '感性体验者'], 'music', '#8b5cf6'),
('《海上钢琴师》', 'movie', '在有限的琴键上，奏出无限的音乐。', ARRAY['感性体验者'], 'piano', '#ef4444');
```

## 更新维护

### 1. 表结构变更
当需要修改表结构时：
1. 创建迁移脚本（如：extend-db-for-portrait.sql）
2. 备份数据
3. 执行迁移
4. 更新本文档

### 2. 规则引擎配置更新
- **添加新原型**: 在soul_prototypes表中插入新记录
- **更新变量**: 修改description_variables表中的values字段
- **调整权重**: 更新soul_prototypes.dimension_weights
- **添加作品**: 在recommended_works表中插入新记录，设置soul_types

### 3. 数据维护
- **用户数据**: 保留2年，匿名用户数据90天后清理
- **事件数据**: 保留1年，按月分区
- **会话数据**: 保留6个月
- **画像数据**: 永久保留（用户灵魂档案）

### 4. 性能监控
- 监控表大小增长
- 监控索引使用情况
- 定期分析查询性能
- 监控规则引擎匹配准确率

## API接口示例

### 1. 生成灵魂画像
```http
POST /api/v1/portrait/generate
Content-Type: application/json

{
  "sessionId": "user-session-123"
}

响应:
{
  "soulType": "深度思考者",
  "description": "你的灵魂在静谧的星空下共鸣...",
  "traits": [
    {"name": "深度内省", "icon": "moon", "color": "#8b5cf6"}
  ],
  "quote": {
    "text": "不要温顺地走进那个良夜...",
    "author": "狄兰·托马斯"
  },
  "recommendations": [
    {
      "type": "movie",
      "title": "《星际穿越》",
      "description": "唯有爱与时间能超越维度...",
      "imageUrl": "https://..."
    }
  ],
  "metadata": {
    "generatedAt": "2026-04-20T18:20:00+08:00",
    "soulId": "0x7E...42C",
    "node": "深空节点"
  }
}
```

### 2. 获取示例画像（开发测试）
```http
GET /api/v1/portrait/example?type=thinker
```

## 技术要点

### 1. 规则引擎设计
- **可配置性**: 所有配置存储在数据库表中
- **可扩展性**: 支持添加新原型、新变量、新作品
- **灵活性**: 描述模板支持变量替换
- **准确性**: 维度权重匹配算法

### 2. 性能优化
- **索引策略**: 为常用查询字段创建索引
- **GIN索引**: 为JSONB和数组字段创建GIN索引
- **缓存机制**: 缓存常用原型配置和作品数据
- **批量处理**: 支持批量生成画像

### 3. 扩展性设计
- **AI集成**: 预留generation_method字段，支持AI版本
- **多语言**: 描述模板和变量支持多语言扩展
- **个性化**: 支持基于用户历史的个性化调整
- **社交功能**: 支持分享、收藏、评论等扩展

---

**文档版本**: v2.0  
**最后更新**: 2026-04-20  
**维护者**: 鞠大毛_mac版（AI儿子）  
**下次评审**: 2026-05-20

## 变更记录

### v2.0 (2026-04-20)
- 新增soul_prototypes表（灵魂原型配置）
- 新增description_variables表（描述模板变量）
- 扩展soul_portraits表（添加规则引擎字段）
- 扩展recommended_works表（添加灵魂类型匹配字段）
- 更新表关系图和索引策略
- 添加规则引擎工作原理说明
- 添加初始数据配置示例

### v1.0 (2026-04-13)
- 初始版本，包含13张表
- 新增soul_gateways表
- 添加AI能力扩展字段
- 完整的数据流分析和索引策略