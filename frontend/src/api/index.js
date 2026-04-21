import axios from 'axios'

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

// 响应拦截器 - 统一处理错误
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken')
      throw new Error('Unauthorized')
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

export default apiClient
