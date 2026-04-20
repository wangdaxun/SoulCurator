import { post, get } from './request'

/**
 * Session管理API
 * 用于创建和管理用户会话
 */

/**
 * 创建新会话
 * @param {string} gatewayType - 灵魂之门入口类型
 * @param {string} screenResolution - 屏幕分辨率
 * @returns {Promise}
 */
export function createSession(gatewayType, screenResolution = null) {
  const requestData = {
    gatewayType,
    screenResolution: screenResolution || `${window.screen.width}x${window.screen.height}`,
  }
  
  return post('/v1/session/create', requestData)
    .then(response => {
      console.debug('会话创建成功:', response.data)
      return response
    })
    .catch(error => {
      console.warn('会话创建失败:', error)
      return Promise.reject(error)
    })
}

/**
 * 验证会话
 * @param {string} sessionId - 会话ID
 * @returns {Promise}
 */
export function validateSession(sessionId) {
  return get(`/v1/session/validate/${sessionId}`)
    .then(response => {
      console.debug('会话验证结果:', response.data)
      return response
    })
    .catch(error => {
      console.warn('会话验证失败:', error)
      return Promise.reject(error)
    })
}

/**
 * 获取或创建会话
 * @param {string} gatewayType - 灵魂之门入口类型
 * @returns {Promise<string>} 会话ID
 */
export async function getOrCreateSession(gatewayType) {
  try {
    // 先尝试从localStorage获取现有会话
    const existingSessionId = localStorage.getItem('soulcurator_session_id')
    
    if (existingSessionId) {
      // 验证现有会话
      const validationResponse = await validateSession(existingSessionId)
      if (validationResponse.data) {
        console.debug('使用现有会话:', existingSessionId)
        return existingSessionId
      } else {
        console.debug('现有会话无效，创建新会话')
        localStorage.removeItem('soulcurator_session_id')
      }
    }
    
    // 创建新会话
    const response = await createSession(gatewayType)
    const sessionId = response.data.sessionId
    
    // 保存到localStorage
    localStorage.setItem('soulcurator_session_id', sessionId)
    
    console.debug('创建新会话:', sessionId)
    return sessionId
    
  } catch (error) {
    console.error('获取或创建会话失败:', error)
    throw error
  }
}

export default {
  createSession,
  validateSession,
  getOrCreateSession,
}