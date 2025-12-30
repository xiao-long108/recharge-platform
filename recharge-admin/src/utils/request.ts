import axios from 'axios'
import { message } from 'ant-design-vue'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '',
  timeout: 30000
})

// 请求拦截器 - 添加 Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('admin_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // 添加 Content-Type
    if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 处理错误
request.interceptors.response.use(
  (response) => {
    // 处理业务逻辑错误（HTTP 200 但 code 不是 200）
    const res = response.data
    if (res && res.code !== undefined && res.code !== 200) {
      // 显示业务错误信息
      message.error(res.message || '操作失败')
      return Promise.reject(new Error(res.message || '操作失败'))
    }
    return response
  },
  (error) => {
    if (error.response) {
      const { status, config } = error.response
      // 如果是登录接口的 401/403 错误,不做特殊处理,让调用方处理
      const isLoginApi = config?.url?.includes('/auth/login')

      if ((status === 401 || status === 403) && !isLoginApi) {
        message.error('登录已过期,请重新登录')
        localStorage.removeItem('admin_token')
        localStorage.removeItem('admin_user')
        router.push('/login')
      } else if (status === 500) {
        message.error('服务器错误')
      }
    } else if (error.code === 'ECONNABORTED') {
      message.error('请求超时,请稍后重试')
    } else if (!error.response) {
      message.error('网络错误,请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
