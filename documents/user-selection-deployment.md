# 用户选择记录接口部署指南

## 1. 后端部署步骤

### 1.1 数据库更新

首先需要更新数据库表结构：

```sql
-- 1. 更新questions表，添加gateway_type字段
ALTER TABLE questions ADD COLUMN IF NOT EXISTS gateway_type VARCHAR(20) NOT NULL DEFAULT 'movie';
ALTER TABLE questions DROP CONSTRAINT IF EXISTS questions_step_number_key;
ALTER TABLE questions ADD CONSTRAINT questions_step_gateway_unique UNIQUE(step_number, gateway_type);
CREATE INDEX IF NOT EXISTS idx_questions_gateway ON questions(gateway_type);

-- 2. 更新options表，添加AI扩展字段
ALTER TABLE options ADD COLUMN IF NOT EXISTS weight JSONB;
ALTER TABLE options ADD COLUMN IF NOT EXISTS next_question_id BIGINT;
ALTER TABLE options ADD COLUMN IF NOT EXISTS ai_context TEXT;

-- 3. 更新user_selections表，添加gateway_type和metadata字段
ALTER TABLE user_selections ADD COLUMN IF NOT EXISTS gateway_type VARCHAR(20) NOT NULL DEFAULT 'movie';
ALTER TABLE user_selections ADD COLUMN IF NOT EXISTS metadata JSONB;
CREATE INDEX IF NOT EXISTS idx_selections_gateway ON user_selections(gateway_type);
```

或者直接运行更新后的 `init-db.sql` 脚本。

### 1.2 后端代码编译

```bash
cd /Users/wangdaxun/SoulCurator/soul-curator-backend

# 清理并重新编译
./mvnw clean compile

# 运行测试
./mvnw test

# 打包
./mvnw package -DskipTests
```

### 1.3 启动后端服务

```bash
# 方式1：使用Maven直接运行
./mvnw spring-boot:run

# 方式2：使用Docker
docker-compose up -d

# 方式3：使用打包的JAR
java -jar target/soul-curator-backend-*.jar
```

## 2. 前端部署步骤

### 2.1 安装依赖

```bash
cd /Users/wangdaxun/SoulCurator/soul-curator-frontend

# 安装依赖
npm install

# 或使用yarn
yarn install
```

### 2.2 配置API代理

确保 `vite.config.js` 中有正确的代理配置：

```javascript
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
```

### 2.3 启动开发服务器

```bash
# 开发模式
npm run dev

# 或构建生产版本
npm run build
npm run preview
```

## 3. 接口测试

### 3.1 使用测试脚本

```bash
cd /Users/wangdaxun/SoulCurator/soul-curator-backend

# 给脚本执行权限
chmod +x test-user-selection-api.sh

# 运行测试
./test-user-selection-api.sh
```

### 3.2 手动测试

#### 测试1：健康检查
```bash
curl -X GET "http://localhost:8080/api/v1/selections/health"
```

#### 测试2：记录用户选择
```bash
curl -X POST "http://localhost:8080/api/v1/selections/record" \
  -H "Content-Type: application/json" \
  -d '{
    "sessionId": "test_session_123",
    "gatewayType": "movie",
    "selections": [
      {
        "questionId": 1,
        "optionId": "deep-reflection",
        "selectedAt": 1744877400000,
        "stepNumber": 1,
        "timeSpentSeconds": 15
      }
    ]
  }'
```

#### 测试3：获取选择记录
```bash
curl -X GET "http://localhost:8080/api/v1/selections?sessionId=test_session_123&gatewayType=movie"
```

#### 测试4：获取选择摘要
```bash
curl -X GET "http://localhost:8080/api/v1/selections/summary?sessionId=test_session_123"
```

#### 测试5：删除选择记录
```bash
curl -X DELETE "http://localhost:8080/api/v1/selections?sessionId=test_session_123"
```

## 4. 集成测试

### 4.1 前端集成测试

1. 启动前后端服务
2. 访问前端页面：`http://localhost:5173`
3. 选择灵魂之门入口
4. 进行灵魂探索，选择选项
5. 查看浏览器控制台，确认选择记录成功

### 4.2 端到端测试流程

```javascript
// 测试用例：完整的灵魂探索流程
1. 用户访问首页
2. 选择"电影之门" (gatewayType: movie)
3. 开始灵魂探索 (生成sessionId)
4. 回答问题1，选择"深刻的人生反思"
5. 回答问题2，选择"人性与道德的边界"
6. 回答问题3，选择"科技与伦理的冲突"
7. 完成探索，生成灵魂画像
8. 验证选择记录已保存到数据库
```

## 5. 监控和日志

### 5.1 后端日志

查看后端日志文件：
```bash
# 查看应用日志
tail -f /Users/wangdaxun/SoulCurator/soul-curator-backend/app.log

# 查看错误日志
grep -i "error\|exception" /Users/wangdaxun/SoulCurator/soul-curator-backend/app.log
```

### 5.2 数据库监控

检查数据库中的数据：
```sql
-- 查看用户选择记录
SELECT * FROM user_selections ORDER BY created_at DESC LIMIT 10;

-- 统计选择数量
SELECT gateway_type, COUNT(*) as count 
FROM user_selections 
GROUP BY gateway_type;

-- 查看会话统计
SELECT session_id, COUNT(*) as selections_count,
       MIN(created_at) as first_selection,
       MAX(created_at) as last_selection
FROM user_selections 
GROUP BY session_id 
ORDER BY last_selection DESC;
```

## 6. 故障排除

### 6.1 常见问题

#### 问题1：数据库连接失败
**症状**: 后端启动失败，报数据库连接错误
**解决**:
```bash
# 检查PostgreSQL是否运行
pg_isready

# 检查数据库配置
cat src/main/resources/application.properties | grep spring.datasource
```

#### 问题2：表结构不匹配
**症状**: 启动时报表或列不存在错误
**解决**:
```bash
# 运行数据库更新脚本
psql -U postgres -d soulcurator -f init-db.sql
```

#### 问题3：前端API调用失败
**症状**: 前端控制台报404或500错误
**解决**:
1. 检查后端服务是否运行：`curl http://localhost:8080/actuator/health`
2. 检查代理配置是否正确
3. 查看浏览器开发者工具的网络请求

#### 问题4：选择记录不保存
**症状**: 前端选择后，数据库中没有记录
**解决**:
1. 检查sessionId是否正确传递
2. 查看后端日志中的错误信息
3. 验证问题和选项ID是否存在

### 6.2 调试技巧

#### 启用详细日志
在 `application.properties` 中添加：
```properties
logging.level.com.soulcurator.backend=DEBUG
logging.level.org.springframework.web=DEBUG
```

#### 使用Swagger UI
访问 `http://localhost:8080/swagger-ui.html` 测试API。

#### 数据库调试
```sql
-- 启用查询日志
ALTER DATABASE soulcurator SET log_statement = 'all';

-- 查看最近的操作
SELECT * FROM pg_stat_activity WHERE datname = 'soulcurator';
```

## 7. 性能优化建议

### 7.1 数据库优化
```sql
-- 添加更多索引
CREATE INDEX idx_selections_session_gateway ON user_selections(session_id, gateway_type);
CREATE INDEX idx_selections_question_option ON user_selections(question_id, option_id);

-- 定期清理旧数据
DELETE FROM user_selections WHERE created_at < NOW() - INTERVAL '90 days';
```

### 7.2 应用层优化
1. 使用连接池配置
2. 启用查询缓存
3. 批量操作优化

### 7.3 前端优化
1. 实现请求重试机制
2. 添加请求防抖
3. 优化本地存储策略

## 8. 安全考虑

### 8.1 输入验证
- 所有输入都经过验证
- 防止SQL注入
- 限制请求大小

### 8.2 会话安全
- sessionId使用足够熵
- 支持会话过期
- 防止会话劫持

### 8.3 数据隐私
- 匿名用户数据隔离
- 敏感信息不记录
- 符合隐私政策

## 9. 扩展计划

### 9.1 短期计划
1. 添加用户画像生成接口
2. 实现选择分析功能
3. 添加管理员面板

### 9.2 长期计划
1. AI驱动的个性化问题
2. 实时推荐系统
3. 多语言支持

## 10. 联系和支持

- **问题反馈**: 创建GitHub Issue
- **紧急支持**: 联系开发团队
- **文档更新**: 维护API文档

---

**部署状态检查清单**:
- [ ] 数据库表结构已更新
- [ ] 后端服务编译通过
- [ ] 前端依赖安装完成
- [ ] API代理配置正确
- [ ] 健康检查通过
- [ ] 基本功能测试通过
- [ ] 集成测试完成
- [ ] 监控配置就绪