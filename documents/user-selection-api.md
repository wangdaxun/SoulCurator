# 用户选择记录API文档

## 概述

用户选择记录API用于记录用户在灵魂探索过程中的选择，支持批量记录、查询、统计和删除功能。

## 基础信息

- **基础路径**: `/api/v1/selections`
- **认证**: 目前支持匿名用户，通过sessionId识别
- **数据格式**: JSON

## API端点

### 1. 健康检查

检查用户选择记录服务是否健康。

**请求**
```
GET /api/v1/selections/health
```

**响应**
```json
{
  "code": 200,
  "message": "用户选择记录服务运行正常",
  "data": "OK",
  "timestamp": 1744877400000
}
```

### 2. 记录用户选择

记录用户在灵魂探索过程中的选择。

**请求**
```
POST /api/v1/selections/record
```

**请求体**
```json
{
  "sessionId": "session_1744877400000_abc123xyz",
  "gatewayType": "movie",
  "selections": [
    {
      "questionId": 1,
      "optionId": "deep-reflection",
      "selectedAt": 1744877400000,
      "stepNumber": 1,
      "timeSpentSeconds": 15,
      "itemMetadata": {
        "confidence": 0.8,
        "hesitation": false
      }
    },
    {
      "questionId": 2,
      "optionId": "humanity-moral-boundary",
      "selectedAt": 1744877401000,
      "stepNumber": 2,
      "timeSpentSeconds": 20,
      "itemMetadata": {
        "confidence": 0.9,
        "hesitation": true
      }
    }
  ],
  "metadata": {
    "deviceInfo": "web",
    "explorationDuration": 120000,
    "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)"
  }
}
```

**字段说明**
- `sessionId`: **必需**，会话ID，用于关联匿名用户
- `gatewayType`: **必需**，灵魂之门入口类型（movie/literature/music/game）
- `selections`: **必需**，选择项数组，至少包含一个选择
  - `questionId`: **必需**，问题ID
  - `optionId`: **必需**，选项ID
  - `selectedAt`: 可选，选择时间戳（毫秒）
  - `stepNumber`: 可选，步骤序号
  - `timeSpentSeconds`: 可选，花费时间（秒）
  - `itemMetadata`: 可选，单个选项的元数据
- `metadata`: 可选，整体元数据

**响应**
```json
{
  "code": 200,
  "message": "选择记录成功",
  "data": {
    "sessionId": "session_1744877400000_abc123xyz",
    "gatewayType": "movie",
    "totalSelections": 2,
    "completedQuestions": 2,
    "totalQuestions": 5,
    "completionPercentage": 40.0,
    "firstSelectionAt": "2026-04-17T09:30:00",
    "lastSelectionAt": "2026-04-17T09:30:01",
    "explorationDurationMs": 1000,
    "selections": [
      {
        "questionId": 1,
        "questionTitle": "你希望在电影中找到什么样的情感共鸣？",
        "optionId": "deep-reflection",
        "optionTitle": "深刻的人生反思",
        "stepNumber": 1,
        "selectedAt": "2026-04-17T09:30:00",
        "timeSpentSeconds": 15
      },
      {
        "questionId": 2,
        "questionTitle": "你更关注哪种主题的深度探讨？",
        "optionId": "humanity-moral-boundary",
        "optionTitle": "人性与道德的边界",
        "stepNumber": 2,
        "selectedAt": "2026-04-17T09:30:01",
        "timeSpentSeconds": 20
      }
    ]
  },
  "timestamp": 1744877402000
}
```

### 3. 获取用户选择记录

根据会话ID获取用户的灵魂探索选择记录。

**请求**
```
GET /api/v1/selections?sessionId={sessionId}&gatewayType={gatewayType}
```

**查询参数**
- `sessionId`: **必需**，会话ID
- `gatewayType`: 可选，灵魂之门入口类型

**响应**
同"记录用户选择"的响应格式。

### 4. 获取用户选择摘要

获取用户选择记录的统计摘要信息。

**请求**
```
GET /api/v1/selections/summary?sessionId={sessionId}
```

**查询参数**
- `sessionId`: **必需**，会话ID

**响应**
```json
{
  "code": 200,
  "message": "获取成功",
  "data": {
    "sessionId": "session_1744877400000_abc123xyz",
    "totalSelections": 3,
    "selectionsByGateway": {
      "movie": 3
    },
    "firstSelectionAt": "2026-04-17T09:30:00",
    "lastSelectionAt": "2026-04-17T09:30:02",
    "explorationDurationMs": 2000,
    "selectedQuestionIds": [1, 2, 3],
    "uniqueQuestionsSelected": 3
  },
  "timestamp": 1744877403000
}
```

### 5. 删除用户选择记录

删除指定会话的用户选择记录。

**请求**
```
DELETE /api/v1/selections?sessionId={sessionId}&gatewayType={gatewayType}
```

**查询参数**
- `sessionId`: **必需**，会话ID
- `gatewayType`: 可选，灵魂之门入口类型（不传则删除所有）

**响应**
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null,
  "timestamp": 1744877404000
}
```

## 错误处理

### 错误响应格式
```json
{
  "code": 400,
  "message": "错误描述",
  "data": null,
  "timestamp": 1744877400000
}
```

### 常见错误码
- `400`: 请求参数错误
- `404`: 资源未找到
- `409`: 数据冲突（如重复选择）
- `500`: 服务器内部错误

### 具体错误场景
1. **缺少必需参数**
   ```json
   {
     "code": 400,
     "message": "会话ID不能为空",
     "data": null,
     "timestamp": 1744877400000
   }
   ```

2. **无效的网关类型**
   ```json
   {
     "code": 400,
     "message": "无效的灵魂之门入口类型: invalid-type",
     "data": null,
     "timestamp": 1744877400000
   }
   ```

3. **问题或选项不存在**
   ```json
   {
     "code": 400,
     "message": "问题不存在: 999",
     "data": null,
     "timestamp": 1744877400000
   }
   ```

4. **选项不属于该问题**
   ```json
   {
     "code": 400,
     "message": "选项不属于该问题: optionId=deep-reflection, questionId=2",
     "data": null,
     "timestamp": 1744877400000
   }
   ```

## 前端集成示例

### 1. 初始化选择记录
```javascript
import { useSelection } from '@/composables/useSelection'
import { getOrCreateSessionId } from '@/api/analytics'

const selection = useSelection()
const sessionId = getOrCreateSessionId()
const gatewayType = 'movie'

// 初始化
selection.initSelection(sessionId, gatewayType)
```

### 2. 记录单个选择
```javascript
async function handleOptionSelected(questionId, optionId) {
  const selectionRecord = {
    questionId,
    optionId,
    optionText: '深刻的人生反思',
    dimension: 'rational',
    weight: 1,
    timeSpentSeconds: 5,
  }
  
  try {
    await selection.recordSelection(selectionRecord)
    console.log('选择记录成功')
  } catch (error) {
    console.warn('选择记录失败:', error)
  }
}
```

### 3. 批量记录选择
```javascript
async function completeExploration(selections) {
  try {
    await selection.recordSelectionsBatch(selections)
    console.log('批量选择记录成功')
  } catch (error) {
    console.warn('批量选择记录失败:', error)
  }
}
```

### 4. 获取选择记录
```javascript
async function loadSelections() {
  try {
    const response = await selection.fetchSelections()
    console.log('选择记录:', response.data.selections)
  } catch (error) {
    console.warn('获取选择记录失败:', error)
  }
}
```

### 5. 离线支持
前端自动支持离线场景：
1. 网络正常时：实时同步到服务器
2. 网络异常时：保存到localStorage
3. 网络恢复时：自动从localStorage恢复并同步

## 数据库表结构

### user_selections表
```sql
CREATE TABLE user_selections (
    id BIGSERIAL PRIMARY KEY,
    -- 关联信息
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_id VARCHAR(64) NOT NULL,
    gateway_type VARCHAR(20) NOT NULL,
    
    -- 选择信息
    question_id BIGINT NOT NULL REFERENCES questions(id),
    option_id VARCHAR(50) NOT NULL REFERENCES options(id),
    
    -- 选择上下文
    step_number INTEGER NOT NULL,
    time_spent_seconds INTEGER,
    
    -- AI扩展字段
    metadata JSONB,
    
    -- 时间戳
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    -- 约束
    UNIQUE(user_id, question_id),
    
    -- 索引
    INDEX idx_selections_user (user_id),
    INDEX idx_selections_session (session_id),
    INDEX idx_selections_gateway (gateway_type),
    INDEX idx_selections_created (created_at)
);
```

## 注意事项

1. **会话管理**: 每个匿名用户应有唯一的sessionId，建议前端生成并持久化
2. **数据一致性**: 同一用户同一问题只能选择一次（UNIQUE约束）
3. **性能考虑**: 支持批量操作，减少HTTP请求
4. **离线支持**: 前端有本地存储机制，确保数据不丢失
5. **扩展性**: metadata字段支持未来功能扩展

## 更新日志

### v1.0.0 (2026-04-17)
- 初始版本发布
- 支持用户选择记录的CRUD操作
- 支持批量记录和离线存储
- 集成前端Composable