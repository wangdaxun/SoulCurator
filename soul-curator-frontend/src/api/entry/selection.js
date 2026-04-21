import { post, get, del } from './../request'

/**
 * 用户选择记录API
 * 用于记录用户在灵魂探索过程中的选择
 */

/**
 * 记录用户选择
 * @param {Object} selectionData - 选择数据
 * @param {string} selectionData.sessionId - 会话ID
 * @param {string} selectionData.gatewayType - 灵魂之门入口类型
 * @param {Array} selectionData.selections - 选择项数组
 * @param {Object} selectionData.metadata - 元数据
 * @returns {Promise}
 */
export function recordSelections({
  sessionId,
  gatewayType,
  selections,
  metadata = {},
}) {
  // 构建请求数据
  const requestData = {
    sessionId,
    gatewayType,
    selections: selections.map((selection, index) => ({
      questionId: selection.questionId,
      optionId: selection.optionId,
      selectedAt: selection.selectedAt || Date.now(),
      stepNumber: selection.stepNumber || index + 1,
      timeSpentSeconds: selection.timeSpentSeconds,
      itemMetadata: selection.metadata || {},
    })),
    metadata: {
      deviceInfo: 'web',
      explorationDuration: metadata.explorationDuration || 0,
      userAgent: navigator.userAgent,
      screenResolution: `${window.screen.width}x${window.screen.height}`,
      language: navigator.language,
      timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      ...metadata,
    },
  }
  
  // 发送请求
  return post('/v1/selections/record', requestData)
    .then(response => {
      console.debug('用户选择记录成功:', response)
      return response
    })
    .catch(error => {
      console.warn('用户选择记录失败:', error)
      // 可以在这里添加重试逻辑或本地存储
      return Promise.reject(error)
    })
}

/**
 * 获取用户选择记录
 * @param {string} sessionId - 会话ID
 * @param {string} gatewayType - 灵魂之门入口类型（可选）
 * @returns {Promise}
 */
export function getSelections(sessionId, gatewayType = null) {
  const params = new URLSearchParams()
  params.append('sessionId', sessionId)
  if (gatewayType) {
    params.append('gatewayType', gatewayType)
  }
  
  return get(`/v1/selections?${params.toString()}`)
    .then(response => {
      console.debug('获取用户选择记录成功:', response)
      return response
    })
    .catch(error => {
      console.warn('获取用户选择记录失败:', error)
      return Promise.reject(error)
    })
}

/**
 * 获取用户选择摘要
 * @param {string} sessionId - 会话ID
 * @returns {Promise}
 */
export function getSelectionSummary(sessionId) {
  const params = new URLSearchParams()
  params.append('sessionId', sessionId)
  
  return get(`/v1/selections/summary?${params.toString()}`)
    .then(response => {
      console.debug('获取用户选择摘要成功:', response)
      return response
    })
    .catch(error => {
      console.warn('获取用户选择摘要失败:', error)
      return Promise.reject(error)
    })
}

/**
 * 删除用户选择记录
 * @param {string} sessionId - 会话ID
 * @param {string} gatewayType - 灵魂之门入口类型（可选）
 * @returns {Promise}
 */
export function deleteSelections(sessionId, gatewayType = null) {
  const params = new URLSearchParams()
  params.append('sessionId', sessionId)
  if (gatewayType) {
    params.append('gatewayType', gatewayType)
  }
  
  return del(`/v1/selections?${params.toString()}`)
    .then(response => {
      console.debug('删除用户选择记录成功:', response)
      return response
    })
    .catch(error => {
      console.warn('删除用户选择记录失败:', error)
      return Promise.reject(error)
    })
}

/**
 * 检查选择记录服务健康状态
 * @returns {Promise}
 */
export function checkSelectionHealth() {
  return get('/v1/selections/health')
    .then(response => {
      console.debug('选择记录服务健康状态:', response)
      return response.status === 'healthy'
    })
    .catch(error => {
      console.warn('选择记录服务健康检查失败:', error)
      return false
    })
}

/**
 * 批量记录选择（性能优化）
 * @param {Array} selections - 选择项数组
 * @param {string} sessionId - 会话ID
 * @param {string} gatewayType - 灵魂之门入口类型
 * @returns {Promise}
 */
export function recordSelectionsBatch(selections, sessionId, gatewayType) {
  return recordSelections({
    sessionId,
    gatewayType,
    selections,
  })
}

/**
 * 记录单个选择
 * @param {Object} selection - 单个选择
 * @param {string} sessionId - 会话ID
 * @param {string} gatewayType - 灵魂之门入口类型
 * @returns {Promise}
 */
export function recordSingleSelection(selection, sessionId, gatewayType) {
  return recordSelections({
    sessionId,
    gatewayType,
    selections: [selection],
  })
}

/**
 * 从本地存储恢复选择记录
 * @param {string} sessionId - 会话ID
 * @param {string} gatewayType - 灵魂之门入口类型
 * @returns {Promise}
 */
export function restoreSelectionsFromStorage(sessionId, gatewayType) {
  try {
    const storageKey = `soulcurator_selections_${sessionId}_${gatewayType}`
    const storedData = localStorage.getItem(storageKey)
    
    if (storedData) {
      const selections = JSON.parse(storedData)
      console.debug('从本地存储恢复选择记录:', selections.length)
      
      // 尝试同步到服务器
      return recordSelectionsBatch(selections, sessionId, gatewayType)
        .then(() => {
          // 同步成功后清除本地存储
          localStorage.removeItem(storageKey)
          return selections
        })
        .catch(error => {
          console.warn('同步选择记录失败，保留在本地存储:', error)
          return selections
        })
    }
    
    return Promise.resolve([])
  } catch (error) {
    console.warn('从本地存储恢复选择记录失败:', error)
    return Promise.resolve([])
  }
}

/**
 * 保存选择记录到本地存储（离线支持）
 * @param {Array} selections - 选择项数组
 * @param {string} sessionId - 会话ID
 * @param {string} gatewayType - 灵魂之门入口类型
 */
export function saveSelectionsToStorage(selections, sessionId, gatewayType) {
  try {
    const storageKey = `soulcurator_selections_${sessionId}_${gatewayType}`
    localStorage.setItem(storageKey, JSON.stringify(selections))
    console.debug('选择记录已保存到本地存储:', selections.length)
  } catch (error) {
    console.warn('保存选择记录到本地存储失败:', error)
  }
}

export function generateSoulPortrait(sessionId, userId = null) {
  return post('/v1/portrait/generate', { sessionId, userId })
    .then(response => {
      console.debug('生成画像成功:', response)
      return response
    })
    .catch(error => {
      console.warn('生成画像失败:', error)
      return Promise.reject(error)
    })
}

export default {
  recordSelections,
  getSelections,
  getSelectionSummary,
  deleteSelections,
  checkSelectionHealth,
  recordSelectionsBatch,
  recordSingleSelection,
  restoreSelectionsFromStorage,
  saveSelectionsToStorage,
}