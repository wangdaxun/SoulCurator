import { ref, computed } from 'vue'
import * as selectionApi from '@/api/entry/selection'
import { useAnalytics } from './useAnalytics'

/**
 * 用户选择记录Composable
 * 提供用户选择记录的相关功能
 */
export function useSelection() {
  const analytics = useAnalytics()
  
  // 状态
  const selections = ref([])
  const isLoading = ref(false)
  const error = ref(null)
  const sessionId = ref(null)
  const gatewayType = ref(null)
  
  // 计算属性
  const totalSelections = computed(() => selections.value.length)
  const hasSelections = computed(() => totalSelections.value > 0)
  const lastSelection = computed(() => {
    if (selections.value.length === 0) return null
    return selections.value[selections.value.length - 1]
  })
  
  /**
   * 初始化选择记录
   */
  function initSelection(sessionIdValue, gatewayTypeValue) {
    sessionId.value = sessionIdValue
    gatewayType.value = gatewayTypeValue
    
    // 尝试从本地存储恢复
    return restoreFromStorage()
  }
  
  /**
   * 记录选择
   */
  async function recordSelection(selection) {
    if (!sessionId.value || !gatewayType.value) {
      throw new Error('请先初始化选择记录（调用initSelection）')
    }
    
    isLoading.value = true
    error.value = null
    
    try {
      // 添加时间戳和步骤信息
      const enrichedSelection = {
        ...selection,
        selectedAt: Date.now(),
        stepNumber: selections.value.length + 1,
      }
      
      // 保存到本地状态
      selections.value.push(enrichedSelection)
      
      // 保存到本地存储（离线支持）
      saveToStorage()
      
      // 记录分析事件
      analytics.recordSelection(
        selection.questionId,
        selection.optionId,
        selection.optionText || '',
        selection.dimension || '',
        selection.weight || 1
      )
      
      // 尝试同步到服务器
      const response = await selectionApi.recordSingleSelection(
        enrichedSelection,
        sessionId.value,
        gatewayType.value
      )
      
      console.debug('选择记录成功:', response)
      return response
      
    } catch (err) {
      error.value = err.message
      console.warn('选择记录失败，已保存到本地存储:', err)
      
      // 即使服务器失败，也返回本地记录
      return {
        success: false,
        message: '选择已保存到本地，稍后将自动同步',
        selection: selection,
      }
      
    } finally {
      isLoading.value = false
    }
  }
  
  /**
   * 批量记录选择
   */
  async function recordSelectionsBatch(selectionList) {
    if (!sessionId.value || !gatewayType.value) {
      throw new Error('请先初始化选择记录（调用initSelection）')
    }
    
    if (!Array.isArray(selectionList) || selectionList.length === 0) {
      throw new Error('选择列表不能为空')
    }
    
    isLoading.value = true
    error.value = null
    
    try {
      // 添加时间戳和步骤信息
      const enrichedSelections = selectionList.map((selection, index) => ({
        ...selection,
        selectedAt: Date.now() + index * 100, // 稍微错开时间戳
        stepNumber: selections.value.length + index + 1,
      }))
      
      // 保存到本地状态
      selections.value.push(...enrichedSelections)
      
      // 保存到本地存储
      saveToStorage()
      
      // 记录分析事件
      enrichedSelections.forEach(selection => {
        analytics.recordSelection(
          selection.questionId,
          selection.optionId,
          selection.optionText || '',
          selection.dimension || '',
          selection.weight || 1
        )
      })
      
      // 尝试同步到服务器
      const response = await selectionApi.recordSelectionsBatch(
        enrichedSelections,
        sessionId.value,
        gatewayType.value
      )
      
      console.debug('批量选择记录成功:', response)
      return response
      
    } catch (err) {
      error.value = err.message
      console.warn('批量选择记录失败，已保存到本地存储:', err)
      
      return {
        success: false,
        message: '选择已保存到本地，稍后将自动同步',
        count: selectionList.length,
      }
      
    } finally {
      isLoading.value = false
    }
  }
  
  /**
   * 获取选择记录
   */
  async function fetchSelections() {
    if (!sessionId.value) {
      throw new Error('会话ID不能为空')
    }
    
    isLoading.value = true
    error.value = null
    
    try {
      const response = await selectionApi.getSelections(
        sessionId.value,
        gatewayType.value
      )
      
      // 更新本地状态
      if (response.data && response.data.selections) {
        selections.value = response.data.selections.map(item => ({
          questionId: item.questionId,
          optionId: item.optionId,
          selectedAt: item.selectedAt ? new Date(item.selectedAt).getTime() : Date.now(),
          stepNumber: item.stepNumber || 1,
          timeSpentSeconds: item.timeSpentSeconds,
          metadata: item.metadata || {},
        }))
      }
      
      console.debug('获取选择记录成功:', selections.value.length)
      return response
      
    } catch (err) {
      error.value = err.message
      console.warn('获取选择记录失败:', err)
      throw err
      
    } finally {
      isLoading.value = false
    }
  }
  
  /** 
   * 获取选择摘要
   */
  async function fetchSelectionSummary() {
    if (!sessionId.value) {
      throw new Error('会话ID不能为空')
    }
    
    isLoading.value = true
    error.value = null
    
    try {
      const response = await selectionApi.getSelectionSummary(sessionId.value)
      console.debug('获取选择摘要成功:', response)
      return response
      
    } catch (err) {
      error.value = err.message
      console.warn('获取选择摘要失败:', err)
      throw err
      
    } finally {
      isLoading.value = false
    }
  }
  
  /**
   * 清除选择记录
   */
  async function clearSelections() {
    if (!sessionId.value) {
      throw new Error('会话ID不能为空')
    }
    
    isLoading.value = true
    error.value = null
    
    try {
      const response = await selectionApi.deleteSelections(
        sessionId.value,
        gatewayType.value
      )
      
      // 清除本地状态
      selections.value = []
      clearStorage()
      
      console.debug('清除选择记录成功')
      return response
      
    } catch (err) {
      error.value = err.message
      console.warn('清除选择记录失败:', err)
      throw err
      
    } finally {
      isLoading.value = false
    }
  }
  
  /**
   * 检查服务健康状态
   */
  async function checkHealth() {
    isLoading.value = true
    
    try {
      const isHealthy = await selectionApi.checkSelectionHealth()
      console.debug('选择记录服务健康状态:', isHealthy)
      return isHealthy
      
    } catch (err) {
      console.warn('检查服务健康状态失败:', err)
      return false
      
    } finally {
      isLoading.value = false
    }
  }
  
  /**
   * 从本地存储恢复
   */
  async function restoreFromStorage() {
    if (!sessionId.value || !gatewayType.value) {
      return
    }
    
    try {
      const restored = await selectionApi.restoreSelectionsFromStorage(
        sessionId.value,
        gatewayType.value
      )
      
      if (restored && restored.length > 0) {
        selections.value = restored
        console.debug('从本地存储恢复选择记录:', restored.length)
      }
      
    } catch (err) {
      console.warn('从本地存储恢复失败:', err)
    }
  }
  
  /**
   * 保存到本地存储
   */
  function saveToStorage() {
    if (!sessionId.value || !gatewayType.value) {
      return
    }
    
    selectionApi.saveSelectionsToStorage(
      selections.value,
      sessionId.value,
      gatewayType.value
    )
  }
  
  /**
   * 清除本地存储
   */
  function clearStorage() {
    if (!sessionId.value || !gatewayType.value) {
      return
    }
    
    const storageKey = `soulcurator_selections_${sessionId.value}_${gatewayType.value}`
    localStorage.removeItem(storageKey)
  }
  
  /**
   * 重置状态
   */
  function reset() {
    selections.value = []
    isLoading.value = false
    error.value = null
    sessionId.value = null
    gatewayType.value = null
  }

  /**
   * 生成画像
   * @returns
   */
  async function generatePortrait() {
    const response = await selectionApi.generateSoulPortrait(sessionId.value)
    return response
  }
  
  return {
    // 状态
    selections,
    isLoading,
    error,
    sessionId,
    gatewayType,
    
    // 计算属性
    totalSelections,
    hasSelections,
    lastSelection,
    
    // 方法
    initSelection,
    recordSelection,
    recordSelectionsBatch,
    fetchSelections,
    fetchSelectionSummary,
    clearSelections,
    checkHealth,
    restoreFromStorage,
    saveToStorage,
    clearStorage,
    reset,
    generatePortrait,
  }
}