import { ref, onMounted, onUnmounted } from 'vue'
import * as analytics from '../api/analytics'

/**
 * Vue3组合式API：分析服务
 * 在组件中方便地使用分析功能
 */

export function useAnalytics() {
  const isAnalyticsReady = ref(false)
  const sessionId = ref(null)
  
  /**
   * 初始化分析服务
   */
  const init = async () => {
    try {
      // 获取会话ID
      sessionId.value = analytics.getOrCreateSessionId()
      
      // 初始化分析服务
      const isHealthy = await analytics.initAnalytics()
      isAnalyticsReady.value = isHealthy
      
      console.log('分析服务初始化完成:', isHealthy ? '正常' : '异常')
      return isHealthy
    } catch (error) {
      console.warn('分析服务初始化失败:', error)
      isAnalyticsReady.value = false
      return false
    }
  }
  
  /**
   * 记录事件（通用方法）
   */
  const recordEvent = (eventData) => {
    if (!isAnalyticsReady.value) {
      console.warn('分析服务未就绪，跳过事件记录:', eventData.eventType)
      return Promise.resolve()
    }
    
    return analytics.recordEvent(eventData)
  }
  
  /**
   * 记录页面浏览
   */
  const recordPageView = (pageName, extraData = {}) => {
    return recordEvent({
      eventType: 'page_view',
      eventName: `${pageName}_entered`,
      eventData: extraData,
    })
  }
  
  /**
   * 记录点击事件
   */
  const recordClick = (elementName, extraData = {}) => {
    return recordEvent({
      eventType: 'click',
      eventName: `${elementName}_clicked`,
      eventData: extraData,
    })
  }
  
  /**
   * 记录选择事件（灵魂探索）
   */
  const recordSelection = (questionId, optionId, optionText, dimension, weight) => {
    return recordEvent({
      eventType: 'selection',
      eventName: 'option_selected',
      eventData: {
        questionId,
        optionId,
        optionText,
        dimension,
        weight,
      },
    })
  }
  
  /**
   * 记录画像生成
   */
  const recordPortraitGenerated = (portraitId, dimensionScores, summary) => {
    return recordEvent({
      eventType: 'portrait_generated',
      eventName: 'soul_portrait_created',
      eventData: {
        portraitId,
        dimensionScores,
        summary,
      },
    })
  }
  
  /**
   * 记录错误事件
   */
  const recordError = (errorType, errorMessage, extraData = {}) => {
    return recordEvent({
      eventType: 'error',
      eventName: `${errorType}_error`,
      eventData: {
        errorMessage,
        ...extraData,
      },
    })
  }
  
  /**
   * 检查服务状态
   */
  const checkHealth = () => {
    return analytics.checkHealth()
  }
  
  /**
   * 获取会话ID
   */
  const getSessionId = () => {
    return sessionId.value
  }
  
  // 组件挂载时自动初始化
  onMounted(() => {
    init()
  })
  
  // 组件卸载时结束会话
  onUnmounted(() => {
    // 注意：这里不调用endSession，因为endSession应该在页面级别调用
    // 组件级别的卸载不需要结束整个会话
  })
  
  return {
    // 状态
    isAnalyticsReady,
    sessionId,
    
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
  }
}

/**
 * 页面浏览跟踪器
 * 自动跟踪路由变化
 */
export function usePageTracker(router) {
  const trackPageView = (to, from) => {
    const pageName = to.name || to.path.replace('/', '').replace(/\//g, '_') || 'unknown'
    
    analytics.recordPageView(pageName, {
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
  const trackClick = (elementName, extraData = {}) => {
    analytics.recordClick(elementName, extraData).catch(() => {
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
  const trackError = (error, errorInfo = {}) => {
    const errorData = {
      message: error.message || String(error),
      stack: error.stack,
      ...errorInfo,
    }
    
    analytics.recordEvent({
      eventType: 'error',
      eventName: 'javascript_error',
      eventData: errorData,
    }).catch(() => {
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

export default {
  useAnalytics,
  usePageTracker,
  useClickTracker,
  useErrorTracker,
}