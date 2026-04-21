# 灵魂画像生成错误诊断

## 错误信息
`Transaction silently rolled back because it has been marked as rollback-only`

## 可能原因

### 1. 数据库字段问题
**检查项**：
- `soul_portraits`表是否有`ai_prompt`字段？
- `soul_portraits`表是否有`match_score`字段（DECIMAL(5,2)）？
- `soul_portraits`表是否有`generation_method`字段？

**解决方案**：
```sql
-- 检查表结构
\d soul_portraits

-- 运行扩展脚本
psql -d soulcurator -f add-ai-prompt-field.sql
```

### 2. JSON字段问题
**检查项**：
- `soul_prototypes.dimension_weights`字段是否是有效的JSON？
- `soul_prototypes.traits`字段是否是有效的JSON？

**解决方案**：
```sql
-- 检查数据
SELECT id, name, dimension_weights, traits FROM soul_prototypes LIMIT 3;

-- 修复无效JSON
UPDATE soul_prototypes 
SET dimension_weights = '{"introspection": 8, "logic": 6, "art": 4, "adventure": 2}'
WHERE name = '深度思考者' AND dimension_weights IS NULL;
```

### 3. 实体类映射问题
**检查项**：
- `SoulPortrait`实体类字段类型与数据库是否匹配？
- 是否有缺少的注解（如`@JdbcTypeCode`）？

**已修复**：
- ✅ `matchScore`字段：`columnDefinition = "DECIMAL(5,2)"`
- ✅ 添加了`@JdbcTypeCode(SqlTypes.JSON)`注解

### 4. 事务处理问题
**已修复**：
- ✅ 移除了`@Transactional`注解
- ✅ 在`savePortraitToDatabase`方法中捕获异常
- ✅ 数据库保存失败不影响画像返回

## 测试步骤

### 步骤1：检查数据库
```bash
# 连接到数据库
psql -d soulcurator

# 检查表结构
\d soul_portraits
\d soul_prototypes

# 检查数据
SELECT * FROM soul_prototypes LIMIT 3;
SELECT COUNT(*) FROM user_selections;
```

### 步骤2：运行SQL脚本
```bash
# 运行扩展脚本
psql -d soulcurator -f add-ai-prompt-field.sql

# 检查是否成功
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_name = 'soul_portraits' 
AND column_name LIKE '%ai%';
```

### 步骤3：测试API
```bash
# 生成测试sessionId
SESSION_ID="test_$(date +%s)"

# 调用API
curl -X POST http://localhost:8080/api/v1/portrait/generate \
  -H "Content-Type: application/json" \
  -d "{\"sessionId\": \"$SESSION_ID\", \"userId\": null}" \
  -v
```

### 步骤4：检查日志
查看VSCode控制台输出，关注：
1. `生成AI提示词，长度: XX 字符`
2. `灵魂画像保存成功，ID: XX, AI提示词已存储`
3. 任何异常堆栈信息

## 快速修复方案

如果问题仍然存在，可以临时禁用数据库保存：

```java
// 在RuleBasedSoulPortraitService.generatePortrait()方法中
// 注释掉数据库保存部分
// savePortraitToDatabase(sessionId, portraitDTO, matchedPrototype, aiPrompt, selections);

// 改为只记录日志
log.info("AI提示词生成完成（数据库保存已禁用），长度: {} 字符", aiPrompt.length());
```

这样至少可以确保API能正常返回画像数据，AI提示词功能可以后续修复。