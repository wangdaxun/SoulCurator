# SoulCurator 前端分析服务集成指南

## 📋 概述

本文档指导前端开发者如何集成和使用SoulCurator的分析服务（埋点功能）。分析服务用于记录用户行为数据，帮助产品优化和用户体验改进。

## 🚀 快速开始

### 1. 安装依赖

确保项目中已安装axios：

```bash
npm install axios
# 或
yarn add axios
```

### 2. 文件结构

```
src/
├── api/
│   ├── analytics.js          # 分析服务API
│   ├── request.js           # 请求封装（已存在）
│   └── works.js            # 作品API（已存在）
├── composables/
│   └── useAnalytics.js     # Vue3组合式API
├── views/
│   └── entry/
│       ├── EntryGuide.vue              # 原始组件
│       └── EntryGuideWithAnalytics.vue # 集成示例
└── main-with-analytics.js  # 集成示例的主入口
```

### 3. 基础使用

#### 3.1 直接调用API

```javascript
import { recordEvent, recordPageView, recordClick } from '@/api/analytics'

// 记录自定义事件
recordEvent({
  eventType: 'click',
  eventName: 'start_button_clicked',
  eventData: {
    buttonId: 'start-btn',
    location: 'welcome_page',
  },
})

// 记录页面浏览
recordPageView('welcome_page', {
  referrer: document.referrer,
  loadTime: 1200,
})

// 记录点击事件
recordClick('help_button', {
  location: 'sidebar',
})
```

#### 3.2 在Vue组件中使用组合式API

```vue
<template>
  <button @click="handleClick">开始探索</button>
</template>

<script setup>
import { useAnalytics } from '@/composables/useAnalytics'

const { recordClick, recordEvent } = useAnalytics()

const handleClick = () => {
  // 记录点击事件
  recordClick('start_exploration_button', {
    buttonText: '开始探索',
    timestamp: Date.now(),
  })
  
  // 记录自定义事件
  recordEvent({
    eventType: 'navigation',
    eventName: 'exploration_started',
    eventData: {
      source: 'welcome_page',
      method: 'button_click',
    },
  })
  
  // 执行业务逻辑
  startExploration()
}
</script>
```

## 📊 核心API

### 1. 事件记录API

#### `recordEvent(options)`
记录自定义事件。

**参数**:
```javascript
{
  eventType: 'page_view',      // 事件类型（必填）
  eventName: 'page_entered',   // 事件名称（必填）
  eventData: {                 // 事件数据（可选）
    key: 'value'
  },
  pagePath: '/welcome',        // 页面路径（可选）
  pageTitle: '欢迎页面',       // 页面标题（可选）
}
```

**常用事件类型**:
- `page_view` - 页面浏览
- `click` - 点击事件
- `selection` - 选择事件
- `portrait_generated` - 画像生成
- `recommendation_view` - 推荐查看
- `feedback_submit` - 反馈提交
- `error` - 错误事件

#### `recordPageView(pageName, extraData)`
记录页面浏览事件（快捷方法）。

#### `recordClick(elementName, extraData)`
记录点击事件（快捷方法）。

#### `recordSelection(questionId, optionId, optionText, dimension, weight)`
记录灵魂探索选择事件。

#### `recordPortraitGenerated(portraitId, dimensionScores, summary)`
记录灵魂画像生成事件。

#### `recordRecommendationView(workId, workType, matchScore)`
记录推荐查看事件。

#### `recordFeedback(recommendationId, feedbackType, comment)`
记录反馈提交事件。

### 2. 工具API

#### `initAnalytics()`
初始化分析服务，应在应用启动时调用。

#### `checkHealth()`
检查分析服务健康状态。

#### `getApiInfo()`
获取API信息。

#### `getOrCreateSessionId()`
获取或创建会话ID。

## 🎯 集成场景

### 场景1：页面浏览跟踪

**自动跟踪（推荐）**:
```javascript
// 在路由配置中添加meta信息
{
  path: '/exploration',
  name: 'Exploration',
  component: ExplorationPage,
  meta: {
    analytics: {
      pageName: 'exploration',
      category: 'main',
    }
  }
}

// 路由会自动记录页面浏览事件
```

**手动跟踪**:
```vue
<script setup>
import { onMounted } from 'vue'
import { useAnalytics } from '@/composables/useAnalytics'

const { recordPageView } = useAnalytics()

onMounted(() => {
  recordPageView('exploration_page', {
    loadTime: window.performance.timing.loadEventEnd - window.performance.timing.navigationStart,
  })
})
</script>
```

### 场景2：按钮点击跟踪

**方法1：直接调用**
```vue
<template>
  <button @click="handleClick">提交</button>
</template>

<script setup>
import { useAnalytics } from '@/composables/useAnalytics'

const { recordClick } = useAnalytics()

const handleClick = () => {
  recordClick('submit_button', {
    formId: 'user_profile',
    isValid: true,
  })
  // ... 其他逻辑
}
</script>
```

**方法2：使用跟踪处理器**
```vue
<template>
  <button @click="trackedHandleClick">提交</button>
</template>

<script setup>
import { useClickTracker } from '@/composables/useAnalytics'

const { createTrackHandler } = useClickTracker()

const handleSubmit = () => {
  // 提交逻辑
}

const trackedHandleClick = createTrackHandler('submit_button', {
  formId: 'user_profile',
}, handleSubmit)
</script>
```

### 场景3：灵魂探索流程

```vue
<template>
  <div v-for="question in questions" :key="question.id">
    <h3>{{ question.title }}</h3>
    <div v-for="option in question.options" :key="option.id">
      <button @click="() => selectOption(question.id, option)">
        {{ option.text }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { useAnalytics } from '@/composables/useAnalytics'

const { recordSelection } = useAnalytics()

const selectOption = (questionId, option) => {
  // 记录选择事件
  recordSelection(
    questionId,
    option.id,
    option.text,
    option.dimension,
    option.weight
  )
  
  // 更新状态
  updateSelection(questionId, option.id)
}
</script>
```

### 场景4：错误跟踪

**自动跟踪全局错误**:
```javascript
// 在main.js中设置
import { useErrorTracker } from '@/composables/useAnalytics'
const errorTracker = useErrorTracker()
errorTracker.setupVueErrorHandler(app)
```

**手动记录错误**:
```javascript
import { useAnalytics } from '@/composables/useAnalytics'

const { recordError } = useAnalytics()

try {
  // 可能出错的代码
  riskyOperation()
} catch (error) {
  // 记录错误
  recordError('operation_failed', error.message, {
    operation: 'risky_operation',
    userId: currentUser.id,
  })
  
  // 显示错误提示
  showError('操作失败，请重试')
}
```

## 🔧 配置选项

### 环境变量

在 `.env` 文件中配置：

```env
# 分析服务地址（默认使用相对路径）
VITE_ANALYTICS_BASE_URL=/api

# 应用版本
VITE_APP_VERSION=1.0.0

# 是否启用分析服务
VITE_ANALYTICS_ENABLED=true

# 开发模式调试
VITE_ANALYTICS_DEBUG=true
```

### 请求配置

在 `src/api/request.js` 中配置：

```javascript
const request = axios.create({
  baseURL: import.meta.env.VITE_ANALYTICS_BASE_URL || '/api',
  timeout: 10000, // 10秒超时
  headers: {
    'Content-Type': 'application/json',
  },
})
```

## 🐛 调试和故障排除

### 1. 开发环境调试

```javascript
// 在浏览器控制台中查看
console.log(window.__SOULCURATOR_ANALYTICS)

// 手动触发事件测试
window.__SOULCURATOR_ANALYTICS.recordEvent({
  eventType: 'test',
  eventName: 'debug_event',
  eventData: { test: true },
})
```

### 2. 常见问题

**问题1：事件记录失败**
```javascript
// 检查网络请求
recordEvent({...}).catch(error => {
  console.error('事件记录失败:', error)
  // 可以在这里添加重试逻辑
  if (error.code === 'NETWORK_ERROR') {
    // 存储到本地，稍后重试
    storeEventLocally(eventData)
  }
})
```

**问题2：会话ID不一致**
```javascript
// 确保使用相同的会话ID
const sessionId = getOrCreateSessionId()
localStorage.setItem('soulcurator_session_id', sessionId)
```

**问题3：页面卸载时事件丢失**
```javascript
// 使用sendBeacon确保页面卸载时也能发送
window.addEventListener('beforeunload', () => {
  endSession() // 会自动使用sendBeacon
})
```

### 3. 监控指标

在浏览器开发者工具的Network标签中：
- 查看 `/api/v1/analytics/events` 请求
- 检查请求状态码（200表示成功）
- 查看请求和响应数据

## 📈 最佳实践

### 1. 事件命名规范

```
格式: [对象]_[动作]_[状态]
示例:
  - page_entered
  - button_clicked
  - option_selected
  - portrait_generated
  - recommendation_viewed
```

### 2. 数据设计原则

```javascript
// 好的数据设计
eventData: {
  questionId: 1,           // 明确的ID
  optionId: 3,            // 明确的ID
  dimension: 'introversion', // 明确的维度
  weight: 0.8,            // 数值类型
  timestamp: Date.now(),  // 时间戳
}

// 避免的数据设计
eventData: {
  data: 'some string',    // 不明确
  info: { nested: true }, // 过于复杂
  undefinedValue: null,   // 空值
}
```

### 3. 性能优化

```javascript
// 1. 批量处理
recordEventsBatch([
  { eventType: 'click', eventName: 'btn1_clicked' },
  { eventType: 'click', eventName: 'btn2_clicked' },
])

// 2. 延迟发送（防抖）
const debouncedRecord = debounce(recordEvent, 300)

// 3. 重要事件立即发送，次要事件可以延迟或批量
```

### 4. 隐私保护

```javascript
// 不要记录敏感信息
// ❌ 错误示例
eventData: {
  email: 'user@example.com',  // 敏感信息
  password: '123456',         // 绝对不要！
  creditCard: '4111...',      // 绝对不要！
}

// ✅ 正确示例
eventData: {
  userId: 123,                // 使用ID而不是个人信息
  userType: 'registered',     // 分类信息
  action: 'purchase_started', // 行为信息
}
```

## 🔄 迁移指南

### 从无分析到有分析

1. **阶段1：基础集成**
   - 添加 `analytics.js` 和 `useAnalytics.js`
   - 在主入口初始化分析服务
   - 添加页面浏览跟踪

2. **阶段2：核心功能跟踪**
   - 跟踪灵魂探索选择
   - 跟踪画像生成
   - 跟踪推荐查看

3. **阶段3：全面跟踪**
   - 跟踪所有按钮点击
   - 跟踪错误和异常
   - 跟踪性能指标

### 从其他分析系统迁移

如果你之前使用其他分析系统（如Google Analytics）：

```javascript
// 包装旧系统，逐步迁移
const recordEventWithLegacy = (eventData) => {
  // 新系统
  recordEvent(eventData)
  
  // 旧系统（逐步移除）
  if (window.ga) {
    window.ga('send', 'event', 
      eventData.category,
      eventData.action,
      eventData.label
    )
  }
}
```

## 📚 扩展阅读

### 相关文件
- `src/api/analytics.js` - 完整的API实现
- `src/composables/useAnalytics.js` - Vue3组合式API
- `backend/docs/analytics-service-layer.md` - 后端服务文档

### 后端接口文档
- `POST /api/v1/analytics/events` - 记录事件
- `GET /api/v1/analytics/health` - 健康检查
- `GET /api/v1/analytics/info` - API信息

### 监控和调试
- 查看浏览器控制台日志
- 使用浏览器开发者工具Network标签
- 后端服务日志（需要后端访问权限）

## 🆘 技术支持

如果遇到问题：

1. **检查控制台错误** - 查看JavaScript错误
2. **检查网络请求** - 查看API调用状态
3. **检查后端日志** - 查看服务端处理情况
4. **联系后端开发** - 如果问题与API相关

## 📝 更新日志

### v1.0.0 (2026-04-14)
- 初始版本发布
- 基础事件记录功能
- Vue3组合式API支持
- 完整的文档和示例

---

**文档维护**: 前端开发团队  
**最后更新**: 2026-04-14  
**版本**: 1.0.0