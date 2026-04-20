import { useAnalyticsStore } from '../stores/analytics.store'

/**
 * Vue3组合式API：分析服务
 * 在组件中方便地使用分析功能
 * 
 * 注意：现在使用 Pinia store 管理状态，状态在应用级别持久化
 */

export function useAnalytics() {
  const analyticsStore = useAnalyticsStore()
  
  /**
   * 初始化分析服务
   * 注意：现在只需要调用一次，状态会在整个应用中保持
   */
  const init = async () => {
    return analyticsStore.init()
  }
  
  /**
   * 记录事件（通用方法）
   */
  const recordEvent = (eventData) => {
    return analyticsStore.recordEvent(eventData)
  }
  
  /**
   * 记录页面浏览
   */
  const recordPageView = (pageName, extraData = {}) => {
    return analyticsStore.recordPageView(pageName, extraData)
  }
  
  /**
   * 记录点击事件
   */
  const recordClick = (elementName, extraData = {}) => {
    return analyticsStore.recordClick(elementName, extraData)
  }
  
  /**
   * 记录选择事件（灵魂探索）
   */
  const recordSelection = (questionId, optionId, optionText, dimension, weight) => {
    return analyticsStore.recordSelection(questionId, optionId, optionText, dimension, weight)
  }
  
  /**
   * 记录画像生成
   */
  const recordPortraitGenerated = (portraitId, dimensionScores, summary) => {
    return analyticsStore.recordPortraitGenerated(portraitId, dimensionScores, summary)
  }
  
  /**
   * 记录错误事件
   */
  const recordError = (errorType, errorMessage, extraData = {}) => {
    return analyticsStore.recordError(errorType, errorMessage, extraData)
  }
  
  /**
   * 检查服务状态
   */
  const checkHealth = () => {
    return analyticsStore.checkHealth()
  }
  
  /**
   * 获取会话ID
   */
  const getSessionId = () => {
    return analyticsStore.getSessionId()
  }
  
  /**
   * 启用/禁用分析服务
   */
  const setEnabled = (enabled) => {
    analyticsStore.setEnabled(enabled)
  }
  
  /**
   * 获取分析服务状态
   */
  const getStatus = () => {
    return {
      isReady: analyticsStore.isAnalyticsReady,
      isEnabled: analyticsStore.isEnabled,
      sessionId: analyticsStore.sessionId,
      healthStatus: analyticsStore.healthStatus,
      successRate: analyticsStore.successRate,
      eventsRecorded: analyticsStore.eventsRecorded,
      eventsFailed: analyticsStore.eventsFailed,
      hasPendingEvents: analyticsStore.hasPendingEvents,
    }
  }
  
  return {
    // 状态（从store中获取）
    isAnalyticsReady: analyticsStore.isAnalyticsReady,
    sessionId: analyticsStore.sessionId,
    isEnabled: analyticsStore.isEnabled,
    
    // 方法
    init,
    recordEvent,
    recordPageView,
    recordClick,
    recordSelection,
    recordPortraitGenerated,
    recordError,
    checkHealth,
    getSessionId,
    setEnabled,
    getStatus,
  }
}

/**
 * 页面浏览跟踪器
 * 自动跟踪路由变化
 * 现在使用 Pinia store 管理状态
 */
export function usePageTracker(router) {
  const analyticsStore = useAnalyticsStore()
  
  const trackPageView = (to, from) => {
    const pageName = to.name || to.path.replace('/', '').replace(/\//g, '_') || 'unknown'
    
    analyticsStore.recordPageView(pageName, {
      from: from?.path || 'direct',
      to: to.path,
      params: to.params,
      query: to.query,
    }).catch(() => {
      // 静默失败
    })
  }
  
  // 监听路由变化
  if (router) {
    router.afterEach(trackPageView)
  }
  
  // 初始页面
  if (router && router.currentRoute.value) {
    trackPageView(router.currentRoute.value)
  }
  
  return {
    trackPageView,
  }
}

/**
 * 点击事件跟踪器
 * 自动跟踪按钮点击
 */
export function useClickTracker() {
  const analyticsStore = useAnalyticsStore()
  
  const trackClick = (elementName, extraData = {}) => {
    analyticsStore.recordClick(elementName, extraData).catch(() => {
      // 静默失败
    })
  }
  
  /**
   * 创建跟踪函数
   */
  const createTrackHandler = (elementName, extraData = {}, originalHandler) => {
    return (...args) => {
      // 先记录点击事件
      trackClick(elementName, extraData)
      
      // 然后执行原始处理函数
      if (originalHandler && typeof originalHandler === 'function') {
        return originalHandler(...args)
      }
    }
  }
  
  return {
    trackClick,
    createTrackHandler,
  }
}

/**
 * 错误跟踪器
 * 自动跟踪JavaScript错误
 */
export function useErrorTracker() {
  const analyticsStore = useAnalyticsStore()
  
  const trackError = (error, errorInfo = {}) => {
    const errorData = {
      message: error.message || String(error),
      stack: error.stack,
      ...errorInfo,
    }
    
    analyticsStore.recordError('javascript', error.message || 'Unknown error', errorData)
      .catch(() => {
        // 静默失败
      })
  }
  
  // 监听全局错误
  if (typeof window !== 'undefined') {
    const originalOnError = window.onerror
    
    window.onerror = (message, source, lineno, colno, error) => {
      trackError(error || new Error(message), {
        source,
        lineno,
        colno,
      })
      
      // 调用原始错误处理
      if (originalOnError) {
        return originalOnError(message, source, lineno, colno, error)
      }
      
      return false
    }
    
    // 监听Promise错误
    window.addEventListener('unhandledrejection', (event) => {
      trackError(event.reason, {
        type: 'unhandledrejection',
      })
    })
  }
  
  // Vue错误处理
  const setupVueErrorHandler = (app) => {
    if (app && app.config) {
      const originalErrorHandler = app.config.errorHandler
      
      app.config.errorHandler = (err, vm, info) => {
        trackError(err, {
          vm: vm?.$options?.name || 'unknown',
          info,
          type: 'vue_error',
        })
        
        // 调用原始错误处理
        if (originalErrorHandler) {
          originalErrorHandler(err, vm, info)
        }
      }
    }
  }
  
  return {
    trackError,
    setupVueErrorHandler,
  }
}

/**
 * 在应用启动时设置分析服务
 * 建议在 main.js 中调用
 */
export function setupAnalytics(app, router) {
  const analyticsStore = useAnalyticsStore()
  
  // 设置错误跟踪
  const errorTracker = useErrorTracker()
  errorTracker.setupVueErrorHandler(app)
  
  // 设置页面跟踪
  if (router) {
    usePageTracker(router)
  }
  
  // 自动初始化分析服务
  if (analyticsStore.isEnabled) {
    analyticsStore.init().catch(error => {
      console.warn('自动初始化分析服务失败:', error)
    })
  }
  
  return analyticsStore
}

export default {
  useAnalytics,
  usePageTracker,
  useClickTracker,
  useErrorTracker,
  setupAnalytics,
}