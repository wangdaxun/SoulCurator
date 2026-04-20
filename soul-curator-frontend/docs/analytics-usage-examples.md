# Analytics 使用示例

## 🎯 概述

现在分析服务使用 Pinia store 管理状态，`isAnalyticsReady` 和 `sessionId` 在整个应用生命周期中保持。这意味着：

1. **单次初始化**: 只需要在应用启动时初始化一次
2. **跨页面共享**: 所有页面都可以访问相同的状态
3. **状态持久化**: 页面刷新后状态仍然保持（通过 localStorage）
4. **更好的错误处理**: 网络不可用时事件会排队等待重试

## 🚀 快速开始

### 1. 在 main.js 中设置分析服务

```javascript
// src/main.js
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { setupAnalytics } from './composables/useAnalytics'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// 设置分析服务（自动初始化）
setupAnalytics(app, router)

app.mount('#app')
```

### 2. 在组件中使用分析服务

#### 示例 1: 页面组件中记录事件

```vue
<!-- src/views/EntryGuide.vue -->
<script setup>
import { useAnalytics } from '@/composables/useAnalytics'
import { onMounted } from 'vue'

const analytics = useAnalytics()

onMounted(() => {
  // 页面加载时记录自定义事件
  analytics.recordEvent({
    eventType: 'custom',
    eventName: 'guide_page_loaded',
    eventData: {
      loadTime: performance.now(),
      userPreference: 'dark'
    }
  })
})

// 记录按钮点击
const handleStartClick = () => {
  analytics.recordClick('start_exploration_button', {
    buttonType: 'primary',
    timestamp: Date.now()
  })
  
  // 继续其他业务逻辑
  router.push('/entry/emotion')
}
</script>

<template>
  <div>
    <h1>灵魂探索指南</h1>
    <button @click="handleStartClick">开始探索</button>
  </div>
</template>
```

#### 示例 2: 答题页面记录选择事件

```vue
<!-- src/components/entry/QuestionCard.vue -->
<script setup>
import { useAnalytics } from '@/composables/useAnalytics'

const props = defineProps({
  question: Object,
  currentIndex: Number
})

const analytics = useAnalytics()

const handleOptionSelect = (option) => {
  // 记录选择事件
  analytics.recordSelection(
    props.question.id,
    option.id,
    option.text,
    props.question.dimension,
    option.weight
  )
  
  // 继续其他业务逻辑
  emit('select', option)
}
</script>

<template>
  <div class="question-card">
    <h3>{{ question.text }}</h3>
    <div v-for="option in question.options" :key="option.id">
      <button @click="handleOptionSelect(option)">
        {{ option.text }}
      </button>
    </div>
  </div>
</template>
```

#### 示例 3: 检查分析服务状态

```vue
<!-- src/components/AnalyticsStatus.vue -->
<script setup>
import { useAnalytics } from '@/composables/useAnalytics'
import { computed } from 'vue'

const analytics = useAnalytics()

const status = computed(() => analytics.getStatus())

const toggleAnalytics = () => {
  analytics.setEnabled(!analytics.isEnabled.value)
}

const retryInit = async () => {
  await analytics.init()
}
</script>

<template>
  <div class="analytics-status">
    <h3>分析服务状态</h3>
    
    <div class="status-item">
      <span>服务状态:</span>
      <span :class="{
        'status-ready': status.isReady,
        'status-not-ready': !status.isReady
      }">
        {{ status.isReady ? '就绪' : '未就绪' }}
      </span>
    </div>
    
    <div class="status-item">
      <span>启用状态:</span>
      <span>{{ status.isEnabled ? '已启用' : '已禁用' }}</span>
    </div>
    
    <div class="status-item">
      <span>会话ID:</span>
      <span class="session-id">{{ status.sessionId || '未初始化' }}</span>
    </div>
    
    <div class="status-item">
      <span>事件成功率:</span>
      <span>{{ status.successRate.toFixed(1) }}%</span>
    </div>
    
    <div class="actions">
      <button @click="toggleAnalytics">
        {{ status.isEnabled ? '禁用分析' : '启用分析' }}
      </button>
      
      <button @click="retryInit" :disabled="status.isReady">
        重新初始化
      </button>
    </div>
  </div>
</template>

<style scoped>
.analytics-status {
  padding: 1rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  background: #f9f9f9;
}

.status-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.status-ready {
  color: green;
  font-weight: bold;
}

.status-not-ready {
  color: orange;
  font-weight: bold;
}

.session-id {
  font-family: monospace;
  font-size: 0.9em;
  color: #666;
}

.actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 1rem;
}

button {
  padding: 0.5rem 1rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: white;
  cursor: pointer;
}

button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
```

## 🔧 高级用法

### 1. 自定义事件类型

```javascript
// 在组件中
const analytics = useAnalytics()

// 记录自定义事件
analytics.recordEvent({
  eventType: 'user_engagement',
  eventName: 'time_spent_on_page',
  eventData: {
    page: 'soul_portrait',
    durationSeconds: 120,
    interactions: 5
  }
})

// 记录转化事件
analytics.recordEvent({
  eventType: 'conversion',
  eventName: 'premium_upgrade',
  eventData: {
    plan: 'premium',
    price: 9.99,
    currency: 'USD'
  }
})
```

### 2. 批量记录事件

```javascript
// 在组件卸载时批量记录
import { onUnmounted } from 'vue'

onUnmounted(() => {
  const events = [
    {
      eventType: 'page_interaction',
      eventName: 'scroll_depth',
      eventData: { depth: 0.8 }
    },
    {
      eventType: 'page_interaction',
      eventName: 'video_play',
      eventData: { videoId: 'intro', duration: 30 }
    }
  ]
  
  // 可以并行发送多个事件
  Promise.all(events.map(event => analytics.recordEvent(event)))
    .then(() => console.log('批量事件记录完成'))
    .catch(() => console.warn('部分事件记录失败'))
})
```

### 3. 错误处理和重试

```javascript
const analytics = useAnalytics()

// 检查服务状态
const checkAndRecord = async (eventData) => {
  if (!analytics.isAnalyticsReady.value) {
    console.warn('分析服务未就绪，稍后重试')
    
    // 可以在这里添加重试逻辑
    setTimeout(() => {
      analytics.recordEvent(eventData)
    }, 5000)
    
    return
  }
  
  try {
    await analytics.recordEvent(eventData)
    console.log('事件记录成功')
  } catch (error) {
    console.error('事件记录失败:', error)
    
    // 根据错误类型处理
    if (error.code === 'NETWORK_ERROR') {
      // 网络错误，可以加入本地队列
      addToLocalQueue(eventData)
    }
  }
}

// 本地队列实现
const addToLocalQueue = (eventData) => {
  const queue = JSON.parse(localStorage.getItem('analytics_queue') || '[]')
  queue.push({
    ...eventData,
    timestamp: Date.now()
  })
  localStorage.setItem('analytics_queue', JSON.stringify(queue))
}
```

### 4. 性能优化

```javascript
// 防抖记录事件，避免频繁发送
import { debounce } from 'lodash-es'

const analytics = useAnalytics()

// 创建防抖的记录函数
const debouncedRecordScroll = debounce((scrollPosition) => {
  analytics.recordEvent({
    eventType: 'user_interaction',
    eventName: 'scroll_position',
    eventData: { position: scrollPosition }
  })
}, 1000)

// 在滚动事件中使用
window.addEventListener('scroll', () => {
  const scrollPosition = window.scrollY / document.body.scrollHeight
  debouncedRecordScroll(scrollPosition)
})
```

## 📊 监控和调试

### 1. 开发工具

```javascript
// 在开发环境中添加调试信息
if (import.meta.env.DEV) {
  // 监听所有分析事件
  const analyticsStore = useAnalyticsStore()
  
  watch(() => analyticsStore.eventsRecorded, (count) => {
    console.log(`已记录 ${count} 个事件`)
  })
  
  watch(() => analyticsStore.pendingEvents, (events) => {
    if (events.length > 0) {
      console.log(`有 ${events.length} 个待处理事件`)
    }
  })
}
```

### 2. 性能监控

```javascript
// 记录性能指标
const recordPerformance = () => {
  if (window.performance && window.performance.timing) {
    const timing = window.performance.timing
    
    const loadTime = timing.loadEventEnd - timing.navigationStart
    const domReadyTime = timing.domContentLoadedEventEnd - timing.navigationStart
    
    analytics.recordEvent({
      eventType: 'performance',
      eventName: 'page_load',
      eventData: {
        loadTime,
        domReadyTime,
        redirectCount: performance.navigation.redirectCount
      }
    })
  }
}

// 在页面加载完成后记录
window.addEventListener('load', recordPerformance)
```

## 🔄 迁移指南

### 从旧版本迁移

如果你之前直接使用 `useAnalytics.js` 的组合式函数，迁移非常简单：

#### 之前：
```javascript
// 旧方式
import { useAnalytics } from '@/composables/useAnalytics'

const { isAnalyticsReady, sessionId, init, recordEvent } = useAnalytics()

onMounted(async () => {
  await init()  // 每个页面都需要初始化
  recordEvent({ /* ... */ })
})
```

#### 现在：
```javascript
// 新方式
import { useAnalytics } from '@/composables/useAnalytics'

const analytics = useAnalytics()

onMounted(() => {
  // 不需要再调用 init()，已经在应用启动时初始化
  // 直接使用状态和方法
  if (analytics.isAnalyticsReady.value) {
    analytics.recordEvent({ /* ... */ })
  }
})
```

### 主要变化

1. **状态持久化**: `isAnalyticsReady` 和 `sessionId` 现在在 Pinia store 中
2. **单次初始化**: 只需要在 `main.js` 中调用一次 `setupAnalytics()`
3. **更好的错误处理**: 网络不可用时事件会自动排队
4. **用户控制**: 用户可以启用/禁用分析服务

## 🎯 最佳实践

### 1. 事件命名规范
- 使用 `snake_case` 命名事件
- 格式: `{页面}_{动作}_{对象}`
- 示例: `guide_page_start_button_clicked`

### 2. 数据格式
```javascript
// 好的示例
analytics.recordEvent({
  eventType: 'user_action',
  eventName: 'question_option_selected',
  eventData: {
    questionId: 'q1',
    optionId: 'o2',
    dimension: 'introspection',
    weight: 0.8,
    timestamp: Date.now()
  }
})

// 避免
analytics.recordEvent({
  type: 'click',  // 不一致的字段名
  name: 'button click',  // 包含空格
  data: { /* 嵌套过深 */ }
})
```

### 3. 隐私考虑
- 不要记录个人身份信息（PII）
- 提供用户禁用选项
- 遵守 GDPR 和其他隐私法规

### 4. 性能考虑
- 使用防抖避免频繁发送
- 批量发送相关事件
- 在空闲时发送低优先级事件

## 🆘 故障排除

### 常见问题

#### 1. 分析服务未初始化
**症状**: `isAnalyticsReady` 始终为 `false`
**解决**: 确保在 `main.js` 中调用了 `setupAnalytics()`

#### 2. 事件记录失败
**症状**: 事件没有发送到服务器
**解决**: 
- 检查网络连接
- 查看浏览器控制台错误
- 检查后端 API 是否可用

#### 3. 会话ID不一致
**症状**: 不同页面的会话ID不同
**解决**: 确保使用 Pinia store，状态会在页面间共享

#### 4. 性能问题
**症状**: 页面响应变慢
**解决**: 
- 使用防抖限制事件频率
- 避免在关键路径中记录事件
- 使用 `requestIdleCallback` 发送低优先级事件

## 📚 相关资源

- [Pinia 文档](https://pinia.vuejs.org/)
- [Vue3 Composition API](https://vuejs.org/guide/extras/composition-api-faq.html)
- [Web 性能监控](https://web.dev/metrics/)
- [隐私最佳实践](https://developers.google.com/analytics/devguides/collection/analyticsjs/user-opt-out)

---

**最后更新**: 2026-04-14  
**维护者**: 前端技术架构师节点