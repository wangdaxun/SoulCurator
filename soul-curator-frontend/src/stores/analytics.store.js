import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as analytics from '../api/analytics'

/**
 * 分析服务状态管理 Store
 * 管理分析服务的状态、会话ID和事件记录
 */
export const useAnalyticsStore = defineStore('analytics', () => {
  // ========== 状态 ==========
  
  /** @type {import('vue').Ref<boolean>} 分析服务是否已就绪 */
  const isAnalyticsReady = ref(false)
  
  /** @type {import('vue').Ref<string|null>} 当前会话ID */
  const sessionId = ref(null)
  
  /** @type {import('vue').Ref<boolean>} 是否正在初始化 */
  const isInitializing = ref(false)
  
  /** @type {import('vue').Ref<Array<Object>>} 待发送的事件队列（网络不可用时） */
  const pendingEvents = ref([])
  
  /** @type {import('vue').Ref<number>} 成功记录的事件数量 */
  const eventsRecorded = ref(0)
  
  /** @type {import('vue').Ref<number>} 失败的事件数量 */
  const eventsFailed = ref(0)
  
  /** @type {import('vue').Ref<Date|null>} 上次成功记录事件的时间 */
  const lastEventTime = ref(null)
  
  /** @type {import('vue').Ref<boolean>} 是否启用分析（用户可控制） */
  const isEnabled = ref(true)
  
  // ========== 计算属性 ==========
  
  /** 分析服务健康状态 */
  const healthStatus = computed(() => {
    if (!isAnalyticsReady.value) return 'not_ready'
    if (eventsFailed.value > 10) return 'degraded'
    return 'healthy'
  })
  
  /** 事件成功率 */
  const successRate = computed(() => {
    const total = eventsRecorded.value + eventsFailed.value
    return total === 0 ? 100 : (eventsRecorded.value / total) * 100
  })
  
  /** 是否有待发送的事件 */
  const hasPendingEvents = computed(() => pendingEvents.value.length > 0)
  
  // ========== 方法 ==========
  
  /**
   * 初始化分析服务
   * @returns {Promise<boolean>} 是否初始化成功
   */
  const init = async () => {
    if (isInitializing.value) {
      console.log('分析服务正在初始化中...')
      return false
    }
    
    if (isAnalyticsReady.value) {
      console.log('分析服务已就绪')
      return true
    }
    
    try {
      isInitializing.value = true
      console.log('开始初始化分析服务...')
      
      // 获取会话ID
      sessionId.value = analytics.getSessionId()
      console.log('会话ID:', sessionId.value)
      
      if (!sessionId.value) {
        console.warn('没有找到有效的会话ID，分析服务将无法记录事件')
        // 这里可以添加逻辑：等待会话创建后再初始化
      }
      
      // 初始化分析服务
      const isHealthy = await analytics.initAnalytics()
      isAnalyticsReady.value = isHealthy
      
      if (isHealthy) {
        console.log('分析服务初始化成功')
        
        // 发送待处理的事件
        if (pendingEvents.value.length > 0) {
          console.log(`发送 ${pendingEvents.value.length} 个待处理事件`)
          await flushPendingEvents()
        }
      } else {
        console.warn('分析服务初始化失败，服务可能不可用')
      }
      
      return isHealthy
    } catch (error) {
      console.error('分析服务初始化异常:', error)
      isAnalyticsReady.value = false
      return false
    } finally {
      isInitializing.value = false
    }
  }
  
  /**
   * 记录事件（核心方法）
   * @param {Object} eventData - 事件数据
   * @returns {Promise<void>}
   */
  const recordEvent = async (eventData) => {
    // 如果分析被禁用，直接返回
    if (!isEnabled.value) {
      console.debug('分析服务被禁用，跳过事件记录')
      return
    }
    
    // 如果服务未就绪，将事件加入队列
    if (!isAnalyticsReady.value) {
      console.debug('分析服务未就绪，事件加入队列:', eventData.eventType)
      pendingEvents.value.push({
        ...eventData,
        timestamp: Date.now(),
        retryCount: 0
      })
      return
    }
    
    try {
      // 添加会话ID到事件数据
      const eventWithSession = {
        ...eventData,
        eventData: {
          ...eventData.eventData,
          sessionId: sessionId.value
        }
      }
      
      await analytics.recordEvent(eventWithSession)
      
      // 更新统计
      eventsRecorded.value++
      lastEventTime.value = new Date()
      
      console.debug('事件记录成功:', eventData.eventType, eventData.eventName)
    } catch (error) {
      console.warn('事件记录失败，加入重试队列:', eventData.eventType, error)
      
      // 将失败事件加入队列
      pendingEvents.value.push({
        ...eventData,
        timestamp: Date.now(),
        retryCount: 0,
        lastError: error.message
      })
      
      eventsFailed.value++
    }
  }
  
  /**
   * 发送待处理的事件
   * @returns {Promise<void>}
   */
  const flushPendingEvents = async () => {
    if (pendingEvents.value.length === 0 || !isAnalyticsReady.value) {
      return
    }
    
    console.log(`开始发送 ${pendingEvents.value.length} 个待处理事件`)
    
    const eventsToSend = [...pendingEvents.value]
    pendingEvents.value = []
    
    const results = await Promise.allSettled(
      eventsToSend.map(event => recordEvent(event))
    )
    
    const succeeded = results.filter(r => r.status === 'fulfilled').length
    const failed = results.filter(r => r.status === 'rejected').length
    
    console.log(`待处理事件发送完成: ${succeeded} 成功, ${failed} 失败`)
  }
  
  /**
   * 记录页面浏览事件
   * @param {string} pageName - 页面名称
   * @param {Object} extraData - 额外数据
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
   * @param {string} elementName - 元素名称
   * @param {Object} extraData - 额外数据
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
   * @param {string} questionId - 问题ID
   * @param {string} optionId - 选项ID
   * @param {string} optionText - 选项文本
   * @param {string} dimension - 影响维度
   * @param {number} weight - 权重
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
        step: Math.ceil(parseInt(questionId) / 3) || 1,
      },
    })
  }
  
  /**
   * 记录画像生成事件
   * @param {string} portraitId - 画像ID
   * @param {Object} dimensionScores - 维度得分
   * @param {string} summary - 画像摘要
   */
  const recordPortraitGenerated = (portraitId, dimensionScores, summary) => {
    return recordEvent({
      eventType: 'portrait_generated',
      eventName: 'soul_portrait_created',
      eventData: {
        portraitId,
        dimensionScores,
        summaryLength: summary?.length || 0,
      },
    })
  }
  
  /**
   * 记录错误事件
   * @param {string} errorType - 错误类型
   * @param {string} errorMessage - 错误信息
   * @param {Object} extraData - 额外数据
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
   * 检查服务健康状态
   * @returns {Promise<boolean>}
   */
  const checkHealth = async () => {
    try {
      const isHealthy = await analytics.checkHealth()
      isAnalyticsReady.value = isHealthy
      return isHealthy
    } catch (error) {
      console.warn('健康检查失败:', error)
      isAnalyticsReady.value = false
      return false
    }
  }
  
  /**
   * 启用/禁用分析服务
   * @param {boolean} enabled - 是否启用
   */
  const setEnabled = (enabled) => {
    isEnabled.value = enabled
    
    // 保存到localStorage，以便下次记住用户选择
    if (typeof localStorage !== 'undefined') {
      localStorage.setItem('analytics_enabled', enabled.toString())
    }
    
    // 如果重新启用，尝试初始化
    if (enabled && !isAnalyticsReady.value) {
      init().catch(() => {
        // 静默失败
      })
    }
  }
  
  /**
   * 重置统计信息
   */
  const resetStats = () => {
    eventsRecorded.value = 0
    eventsFailed.value = 0
    pendingEvents.value = []
    lastEventTime.value = null
  }
  
  /**
   * 获取会话ID
   * @returns {string|null}
   */
  const getSessionId = () => sessionId.value
  
  /**
   * 手动设置会话ID（用于测试或特定场景）
   * @param {string} id - 会话ID
   */
  const setSessionId = (id) => {
    sessionId.value = id
  }
  
  // ========== 初始化 ==========
  
  // 从localStorage加载用户设置
  if (typeof localStorage !== 'undefined') {
    const savedEnabled = localStorage.getItem('analytics_enabled')
    if (savedEnabled !== null) {
      isEnabled.value = savedEnabled === 'true'
    }
  }
  
  return {
    // 状态
    isAnalyticsReady,
    sessionId,
    isInitializing,
    pendingEvents,
    eventsRecorded,
    eventsFailed,
    lastEventTime,
    isEnabled,
    
    // 计算属性
    healthStatus,
    successRate,
    hasPendingEvents,
    
    // 方法
    init,
    recordEvent,
    recordPageView,
    recordClick,
    recordSelection,
    recordPortraitGenerated,
    recordError,
    checkHealth,
    setEnabled,
    resetStats,
    getSessionId,
    setSessionId,
    flushPendingEvents,
  }
})

/**
 * 在应用启动时自动初始化分析服务
 * 建议在 main.js 中调用
 */
export function setupAnalytics() {
  const store = useAnalyticsStore()
  
  // 如果分析被启用，自动初始化
  if (store.isEnabled) {
    store.init().catch(error => {
      console.warn('自动初始化分析服务失败:', error)
    })
  }
  
  return store
}

export default {
  useAnalyticsStore,
  setupAnalytics,
}