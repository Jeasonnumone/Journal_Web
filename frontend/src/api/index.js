import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const API_BASE_URL = 'http://localhost:8081'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // 允许携带 Cookie
})

// 请求拦截器 - 自动添加 Access Token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 根据自定义业务码处理
apiClient.interceptors.response.use(
  (response) => {
    const { code, message } = response.data
    
    // 成功响应
    if (code === 200) {
      return response
    }
    
    // 根据自定义业务码处理
    switch (code) {
      case 4010: // 未登录/登录过期
        localStorage.removeItem('accessToken')
        router.push('/login')
        ElMessage.error('登录已过期，请重新登录')
        break
        
      case 4011: // Token 无效
        localStorage.removeItem('accessToken')
        router.push('/login')
        ElMessage.error('登录状态无效')
        break
        
      case 4030: // 权限不足
        ElMessage.error('权限不足，无法操作')
        break
        
      case 4040: // 资源不存在
        ElMessage.error('资源不存在')
        break
        
      default:
        // 其他业务错误
        ElMessage.error(message || '操作失败')
    }
    
    return Promise.reject(response.data)
  },
  (error) => {
    // 网络错误等非业务错误
    if (!error.response) {
      ElMessage.error('网络错误，请检查网络连接')
    } else {
      ElMessage.error('服务器错误，请稍后重试')
    }
    return Promise.reject(error)
  }
)

// 登录
export const login = (data) => {
  return apiClient.post('/api/auth/login', data)
}

// 注册
export const register = (data) => {
  return apiClient.post('/api/auth/register', data)
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return apiClient.get('/api/auth/me')
}

// 刷新 Token（从 Cookie 中获取 Refresh Token）
export const refreshToken = () => {
  return apiClient.post('/api/auth/refresh')
}

// 退出登录
export const logout = () => {
  return apiClient.post('/api/auth/logout')
}

// 发送注册验证码
export const sendVerifyCode = (email) => {
  return apiClient.post('/api/auth/verify-code', { email })
}

// 获取期刊列表
export const getJournals = (params = {}) => {
  return apiClient.get('/api/journals', { params })
}

// 获取期刊分类
export const getCategories = () => {
  return apiClient.get('/api/journals/categories')
}

// 获取期刊详情
export const getJournalById = (id) => {
  return apiClient.get(`/api/journals/${id}`)
}

// 获取评论列表（根评论）
export const getRootComments = (journalId, page = 1, pageSize = 10) => {
  return apiClient.get(`/api/comments/journal/${journalId}`, {
    params: { page, pageSize }
  })
}

// 获取回复列表
export const getReplies = (rootId, page = 1, pageSize = 20) => {
  return apiClient.get(`/api/comments/${rootId}/replies`, {
    params: { page, pageSize }
  })
}

// 发表评论/回复
export const createComment = (data) => {
  return apiClient.post('/api/comments', data)
}

// 删除评论
export const deleteComment = (commentId) => {
  return apiClient.delete(`/api/comments/${commentId}`)
}

// 获取最新评论（跨期刊）
export const getRecentComments = (limit = 10) => {
  return apiClient.get('/api/comments/recent', { params: { limit } })
}

export default apiClient
