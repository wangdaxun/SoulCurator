import { get } from './request'

/**
 * 获取作品列表
 * @returns {Promise<Array>}
 */
export function getWorks() {
  return get('/works')
}