# 灵魂画像生成功能 - 实现文档

## 概述

本文档描述了灵魂画像生成功能的实现方案，基于规则引擎生成个性化的灵魂画像和作品推荐。

## 一、技术架构

### 1.1 核心组件

```
Frontend (Vue3)
    ↓ HTTP请求
Backend (Spring Boot)
    ↓ 规则引擎
RuleBasedSoulPortraitService
    ↓ 数据访问
Database (PostgreSQL)
```

### 1.2 数据流

```
用户选择 → 维度权重计算 → 原型匹配 → 画像生成 → 作品推荐
```

## 二、数据库扩展

### 2.1 新增表

#### 1. soul_prototypes (灵魂原型配置表)
- `name`: 原型名称（如：深度思考者）
- `description_template`: 描述模板（支持变量替换）
- `dimension_weights`: 维度权重配置（JSONB）
- `traits`: 特质列表（JSONB）
- `quotes`: 引用列表（JSONB）

#### 2. description_variables (描述模板变量表)
- `variable_key`: 变量键（如：${adjective}）
- `variable_type`: 变量类型（adjective/time/theme）
- `values`: 变量值数组（JSONB）

### 2.2 扩展表

#### 1. recommended_works 表新增字段
- `soul_types`: 匹配的灵魂类型数组
- `icon`: 图标名称
- `color_hex`: 颜色代码
- `display_order`: 显示顺序

#### 2. soul_portraits 表新增字段
- `traits`: 特质列表（JSONB）
- `quote`: 引用信息（JSONB）
- `metadata`: 元数据（JSONB）
- `generation_method`: 生成方法（rule_engine/ai）
- `match_score`: 匹配度分数

## 三、Java实现

### 3.1 DTO类（数据传输对象）

1. **SoulPortraitDTO** - 灵魂画像完整响应
2. **TraitDTO** - 特质信息
3. **QuoteDTO** - 引用信息
4. **RecommendationDTO** - 推荐作品
5. **MetadataDTO** - 元数据
6. **GeneratePortraitRequest** - 生成请求

### 3.2 服务类

#### RuleBasedSoulPortraitService
核心规则引擎，包含以下功能：

1. **维度权重计算**
   - 分析用户选择记录
   - 计算各维度的累计得分

2. **原型匹配算法**
   - 预定义多个灵魂原型
   - 根据维度权重匹配最接近的原型
   - 相似度计算：权重匹配 + 维度覆盖

3. **画像生成**
   - 使用模板生成个性化描述
   - 随机选择引用
   - 生成特质列表

4. **作品推荐**
   - 根据原型匹配推荐作品
   - 从数据库或硬编码数据获取

### 3.3 控制器

#### SoulPortraitController
提供两个API端点：

1. **POST /api/v1/portrait/generate**
   - 输入：sessionId
   - 输出：完整的灵魂画像

2. **GET /api/v1/portrait/example**
   - 输入：type（thinker/feeler/adventurer）
   - 输出：示例灵魂画像（用于前端测试）

## 四、部署步骤

### 4.1 数据库部署

```bash
# 1. 运行数据库扩展脚本
psql -U postgres -d soulcurator -f extend-db-for-portrait.sql

# 2. 验证数据
SELECT * FROM soul_prototypes;
SELECT * FROM description_variables;
SELECT title, soul_types FROM recommended_works LIMIT 5;
```

### 4.2 后端部署

```bash
# 1. 编译项目
cd /Users/wangdaxun/SoulCurator/soul-curator-backend
./mvnw clean compile

# 2. 运行测试
./mvnw test

# 3. 启动服务
./mvnw spring-boot:run
```

### 4.3 测试API

```bash
# 1. 测试示例接口
./test-portrait-api.sh thinker

# 2. 测试生成接口（需要有效sessionId）
curl -X POST http://localhost:8080/api/v1/portrait/generate \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "your-session-id"}'
```

## 五、前端集成

### 5.1 API调用

```javascript
// 生成灵魂画像
async function generateSoulPortrait(sessionId) {
  const response = await fetch('/api/v1/portrait/generate', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ sessionId })
  });
  
  const portrait = await response.json();
  return portrait;
}

// 获取示例（开发测试用）
async function getExamplePortrait(type = 'thinker') {
  const response = await fetch(`/api/v1/portrait/example?type=${type}`);
  return await response.json();
}
```

### 5.2 响应数据结构

```json
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

## 六、配置与扩展

### 6.1 灵魂原型配置

可以通过数据库`soul_prototypes`表动态配置：

1. **添加新原型**
   ```sql
   INSERT INTO soul_prototypes (name, description_template, dimension_weights, traits, quotes)
   VALUES (...);
   ```

2. **修改现有原型**
   ```sql
   UPDATE soul_prototypes 
   SET dimension_weights = '{"new_dimension": 5}'
   WHERE name = '深度思考者';
   ```

### 6.2 描述变量扩展

1. **添加新变量类型**
   ```sql
   INSERT INTO description_variables (variable_key, variable_type, values)
   VALUES ('${new_var}', 'new_type', '["value1", "value2"]');
   ```

2. **更新变量值**
   ```sql
   UPDATE description_variables 
   SET values = '["新值1", "新值2"]'
   WHERE variable_key = '${adjective}';
   ```

### 6.3 作品推荐扩展

1. **添加新作品**
   ```sql
   INSERT INTO recommended_works (title, type, description, soul_types, ...)
   VALUES ('新作品', 'movie', '描述', ARRAY['深度思考者', '感性体验者'], ...);
   ```

2. **更新作品匹配**
   ```sql
   UPDATE recommended_works 
   SET soul_types = ARRAY['新原型']
   WHERE title = '作品名';
   ```

## 七、未来扩展

### 7.1 AI集成

预留接口用于未来AI版本：

```java
interface SoulPortraitGenerator {
    SoulPortraitDTO generate(String sessionId);
}

class RuleBasedGenerator implements SoulPortraitGenerator { ... }
class AIGenerator implements SoulPortraitGenerator { ... } // 未来实现
```

### 7.2 高级功能

1. **多维度画像**：支持混合型灵魂画像
2. **动态权重**：根据用户行为调整权重计算
3. **个性化推荐**：基于用户历史反馈优化推荐
4. **社交分享**：生成可分享的图片/卡片

## 八、故障排除

### 8.1 常见问题

1. **API返回400错误**
   - 检查sessionId是否有效
   - 确认用户有选择记录

2. **画像描述不准确**
   - 检查维度权重配置
   - 验证原型匹配算法

3. **推荐作品为空**
   - 检查recommended_works表数据
   - 验证soul_types字段匹配

### 8.2 日志查看

```bash
# 查看后端日志
tail -f /Users/wangdaxun/SoulCurator/soul-curator-backend/app.log

# 查看数据库日志（如果配置了）
sudo tail -f /var/log/postgresql/postgresql-*.log
```

## 九、性能考虑

1. **缓存策略**
   - 缓存原型配置
   - 缓存常用作品推荐

2. **数据库优化**
   - 为soul_types字段创建GIN索引
   - 定期清理旧画像数据

3. **API优化**
   - 支持批量生成
   - 添加分页查询

---

**文档版本**: 1.0  
**最后更新**: 2026-04-20  
**维护者**: 鞠大毛_mac版（AI儿子）