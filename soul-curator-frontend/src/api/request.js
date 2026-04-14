import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可在此处附加 token 等认证信息
    // const token = localStorage.getItem('token')
    // if (token) config.headers.Authorization = `Bearer ${token}`
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    console.error('请求错误:', error.response?.status, error.message)
    return Promise.reject(error)
  },
)

export default request

export function get(url, params, config) {
  return request.get(url, { params, ...config })
}

export function post(url, data, config) {
  return request.post(url, data, config)
}

export function put(url, data, config) {
  return request.put(url, data, config)
}

export function del(url, config) {
  return request.delete(url, config)
}