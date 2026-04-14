import { post, get } from './request'

/**
 * 分析服务API
 * 用于记录用户行为事件和获取分析数据
 */

// 会话ID管理
let sessionId = null

/**
 * 生成或获取会话ID
 */
export function getOrCreateSessionId() {
  if (!sessionId) {
    // 尝试从localStorage获取
    sessionId = localStorage.getItem('soulcurator_session_id')
    
    // 如果没有，生成新的会话ID
    if (!sessionId) {
      sessionId = generateSessionId()
      localStorage.setItem('soulcurator_session_id', sessionId)
    }
  }
  
  return sessionId
}

/**
 * 生成会话ID
 */
function generateSessionId() {
  return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
}

/**
 * 记录用户事件
 * @param {Object} eventData - 事件数据
 * @param {string} eventData.eventType - 事件类型
 * @param {string} eventData.eventName - 事件名称
 * @param {Object} eventData.eventData - 事件详细数据
 * @param {string} eventData.pagePath - 页面路径
 * @param {string} eventData.pageTitle - 页面标题
 * @returns {Promise}
 */
export function recordEvent({
  eventType,
  eventName,
  eventData = {},
  pagePath = window.location.pathname,
  pageTitle = document.title,
}) {
  // 确保有会话ID
  const sessionId = getOrCreateSessionId()
  
  // 构建完整的事件数据
  const fullEventData = {
    sessionId,
    ...eventData,
  }
  
  // 构建请求数据
  const requestData = {
    eventType,
    eventName,
    eventData: fullEventData,
    pagePath,
    pageTitle,
    userAgent: navigator.userAgent,
    screenResolution: `${window.screen.width}x${window.screen.height}`,
    language: navigator.language,
    timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
    timestamp: Date.now(),
  }
  
  // 发送请求
  return post('/v1/analytics/events', requestData)
    .then(response => {
      console.debug('事件记录成功:', eventType, eventName, response)
      return response
    })
    .catch(error => {
      console.warn('事件记录失败:', eventType, eventName, error)
      // 可以在这里添加重试逻辑或本地存储
      return Promise.reject(error)
    })
}

/**
 * 记录页面浏览事件
 * @param {string} pageName - 页面名称
 * @param {Object} extraData - 额外数据
 */
export function recordPageView(pageName, extraData = {}) {
  return recordEvent({
    eventType: 'page_view',
    eventName: `${pageName}_entered`,
    eventData: {
      ...extraData,
      referrer: document.referrer,
    },
  })
}

/**
 * 记录点击事件
 * @param {string} elementName - 元素名称
 * @param {Object} extraData - 额外数据
 */
export function recordClick(elementName, extraData = {}) {
  return recordEvent({
    eventType: 'click',
    eventName: `${elementName}_clicked`,
    eventData: extraData,
  })
}

/**
 * 记录选择事件（用于灵魂探索）
 * @param {number} questionId - 问题ID
 * @param {number} optionId - 选项ID
 * @param {string} optionText - 选项文本
 * @param {string} dimension - 影响维度
 * @param {number} weight - 权重
 */
export function recordSelection(questionId, optionId, optionText, dimension, weight) {
  return recordEvent({
    eventType: 'selection',
    eventName: 'option_selected',
    eventData: {
      questionId,
      optionId,
      optionText,
      dimension,
      weight,
      step: Math.ceil(questionId / 3), // 假设每3个问题一个步骤
    },
  })
}

/**
 * 记录画像生成事件
 * @param {number} portraitId - 画像ID
 * @param {Object} dimensionScores - 维度得分
 * @param {string} summary - 画像摘要
 */
export function recordPortraitGenerated(portraitId, dimensionScores, summary) {
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
 * 记录推荐查看事件
 * @param {number} workId - 作品ID
 * @param {string} workType - 作品类型
 * @param {number} matchScore - 匹配分数
 */
export function recordRecommendationView(workId, workType, matchScore) {
  return recordEvent({
    eventType: 'recommendation_view',
    eventName: 'recommendation_displayed',
    eventData: {
      workId,
      workType,
      matchScore,
    },
  })
}

/**
 * 记录反馈提交事件
 * @param {number} recommendationId - 推荐ID
 * @param {string} feedbackType - 反馈类型（like/dislike/neutral）
 * @param {string} comment - 评论
 */
export function recordFeedback(recommendationId, feedbackType, comment = '') {
  return recordEvent({
    eventType: 'feedback_submit',
    eventName: 'recommendation_feedback',
    eventData: {
      recommendationId,
      feedbackType,
      commentLength: comment.length,
    },
  })
}

/**
 * 检查分析服务健康状态
 * @returns {Promise}
 */
export function checkHealth() {
  return get('/v1/analytics/health')
    .then(response => {
      console.debug('分析服务健康状态:', response)
      return response === 'Analytics API is healthy'
    })
    .catch(error => {
      console.warn('分析服务健康检查失败:', error)
      return false
    })
}

/**
 * 获取API信息
 * @returns {Promise}
 */
export function getApiInfo() {
  return get('/v1/analytics/info')
    .then(response => {
      console.debug('API信息:', response)
      return response
    })
    .catch(error => {
      console.warn('获取API信息失败:', error)
      return null
    })
}

/**
 * 批量记录事件（性能优化）
 * @param {Array} events - 事件数组
 */
export function recordEventsBatch(events) {
  // 注意：后端需要支持批量接口
  // 当前实现是循环调用单个接口
  return Promise.allSettled(
    events.map(event => recordEvent(event))
  )
}

/**
 * 初始化分析服务
 * @returns {Promise}
 */
export function initAnalytics() {
  console.log('初始化分析服务...')
  
  // 记录会话开始
  recordEvent({
    eventType: 'session_start',
    eventName: 'session_started',
    eventData: {
      platform: 'web',
      url: window.location.href,
    },
  }).catch(() => {
    // 静默失败，不影响用户体验
  })
  
  // 记录初始页面浏览
  recordPageView('initial_load', {
    loadTime: window.performance.timing.loadEventEnd - window.performance.timing.navigationStart,
  }).catch(() => {
    // 静默失败
  })
  
  // 检查服务健康状态
  return checkHealth().then(isHealthy => {
    if (!isHealthy) {
      console.warn('分析服务不可用，事件记录可能失败')
    }
    return isHealthy
  })
}

/**
 * 结束会话（页面卸载时调用）
 */
export function endSession() {
  // 使用sendBeacon确保在页面卸载时也能发送
  const sessionId = getOrCreateSessionId()
  const data = {
    eventType: 'session_end',
    eventName: 'session_ended',
    eventData: { sessionId },
    timestamp: Date.now(),
  }
  
  // 尝试使用sendBeacon
  if (navigator.sendBeacon) {
    const blob = new Blob([JSON.stringify(data)], { type: 'application/json' })
    navigator.sendBeacon('/api/v1/analytics/events', blob)
  } else {
    // 回退方案
    recordEvent(data).catch(() => {})
  }
}

// 页面卸载时自动结束会话
if (typeof window !== 'undefined') {
  window.addEventListener('beforeunload', endSession)
  window.addEventListener('pagehide', endSession)
}

export default {
  getOrCreateSessionId,
  recordEvent,
  recordPageView,
  recordClick,
  recordSelection,
  recordPortraitGenerated,
  recordRecommendationView,
  recordFeedback,
  checkHealth,
  getApiInfo,
  recordEventsBatch,
  initAnalytics,
  endSession,
}