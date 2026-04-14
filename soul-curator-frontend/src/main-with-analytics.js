import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'

// 导入分析服务
import * as analytics from './api/analytics'
import { useErrorTracker } from './composables/useAnalytics'

const app = createApp(App)

// 使用Pinia状态管理
app.use(createPinia())

// 使用路由
app.use(router)

// 设置错误跟踪
const errorTracker = useErrorTracker()
errorTracker.setupVueErrorHandler(app)

// 应用挂载前初始化分析服务
const initAnalytics = async () => {
  console.log('初始化分析服务...')
  
  try {
    // 初始化分析服务
    const isHealthy = await analytics.initAnalytics()
    
    if (isHealthy) {
      console.log('✅ 分析服务初始化成功')
      
      // 记录应用启动事件
      analytics.recordEvent({
        eventType: 'app_start',
        eventName: 'application_started',
        eventData: {
          version: import.meta.env.VITE_APP_VERSION || '1.0.0',
          environment: import.meta.env.MODE,
          platform: 'web',
          url: window.location.href,
        },
      }).catch(() => {
        // 静默失败
      })
    } else {
      console.warn('⚠️ 分析服务不可用，事件记录可能失败')
    }
  } catch (error) {
    console.error('❌ 分析服务初始化失败:', error)
  }
}

// 启动应用
const startApp = async () => {
  // 初始化分析服务（不阻塞应用启动）
  initAnalytics().catch(() => {
    // 即使分析服务失败，也不影响应用启动
  })
  
  // 挂载应用
  app.mount('#app')
  
  console.log('🚀 应用已启动')
}

// 启动应用
startApp()

// 导出app实例（用于测试等）
export { app }

// 开发环境调试
if (import.meta.env.DEV) {
  // 将分析服务挂载到window，方便调试
  window.__SOULCURATOR_ANALYTICS = analytics
  
  // 监听路由变化（备用方案）
  router.afterEach((to, from) => {
    const pageName = to.name || to.path.replace('/', '').replace(/\//g, '_') || 'unknown'
    
    analytics.recordPageView(pageName, {
      from: from?.path || 'direct',
      to: to.path,
      params: to.params,
      query: to.query,
    }).catch(() => {
      // 静默失败
    })
  })
}