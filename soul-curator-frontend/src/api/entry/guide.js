import { post, get } from './../request'
/**
 * 用于获取灵魂之门数据和开始灵魂探索
 */
export function getAllSoulGateways() {
  return get('/v1/soul-gateways/all')
    .then((response) => {
      console.log(response)
      return response
    })
    .catch((error) => {
      console.error('请求灵魂之门数据出错:', error)
      return null
    })
}

/**
 * 根据门类型获取灵魂探索数据
 * @param {*} gatewayType 
 * @returns 
 */
export function getSoulExpByGatewayType(gatewayType) {
  return post('/v1/soul-exploration/start', { gatewayType }  )
    .then((response) => {
      console.log(response)
      return response
    })
    .catch((error) => {
      console.error('请求灵魂探索数据出错:', error)
      return null
    })
}
