import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'
import { setupAnalytics } from './composables/useAnalytics'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

// 设置分析服务（自动初始化）
// 这会：
// 1. 自动初始化分析服务
// 2. 设置错误跟踪
// 3. 设置页面浏览跟踪
// 4. 启用点击跟踪
setupAnalytics(app, router)

app.mount('#app')

// 可选：在开发环境中添加调试信息
if (import.meta.env.DEV) {
  // 监听分析服务状态变化
  import('./stores/analytics.store.js').then(({ useAnalyticsStore }) => {
    const analyticsStore = useAnalyticsStore()
    
    // 监听状态变化
    analyticsStore.$subscribe((mutation, state) => {
      console.log('Analytics Store 状态变化:', {
        isReady: state.isAnalyticsReady,
        sessionId: state.sessionId,
        eventsRecorded: state.eventsRecorded,
        pendingEvents: state.pendingEvents.length
      })
    })
    
    // 定期检查服务状态
    setInterval(() => {
      if (analyticsStore.isAnalyticsReady) {
        analyticsStore.checkHealth().then(isHealthy => {
          if (!isHealthy) {
            console.warn('分析服务健康检查失败，尝试重新初始化')
            analyticsStore.init().catch(() => {
              console.warn('重新初始化失败')
            })
          }
        })
      }
    }, 60000) // 每分钟检查一次
  })
}