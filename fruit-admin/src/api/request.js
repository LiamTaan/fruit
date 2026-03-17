import axios from 'axios'

// 创建axios实例
const request = axios.create({
  baseURL: '/api', // 后端API地址（相对路径，通过Vite代理转发）
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    // 从localStorage中获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求拦截器错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 处理响应数据结构
    const res = response.data
    
    // 如果是对象类型的响应
    if (typeof res === 'object' && res !== null) {
      // 优先检查是否有data字段，如果有则返回data字段
      if (res.data) {
        return res.data
      }
      // 如果有records字段，直接返回原数据（兼容分页接口）
      if (res.records) {
        return res
      }
    }
    
    // 否则直接返回原数据
    return res
  },
  error => {
    console.error('响应拦截器错误:', error)
    return Promise.reject(error)
  }
)

export default request
