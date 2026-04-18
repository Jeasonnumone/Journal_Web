const API_BASE_URL = 'http://localhost:8081'

// 通用请求方法
const request = async (url, options = {}) => {
  const token = localStorage.getItem('token')
  
  const defaultHeaders = {
    'Content-Type': 'application/json',
  }
  
  if (token) {
    defaultHeaders['Authorization'] = `Bearer ${token}`
  }
  
  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers,
    },
  })
  
  const result = await response.json()
  
  if (response.status === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/login'
    return null
  }
  
  return result
}

// 登录
export const login = (data) => {
  return request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

// 注册
export const register = (data) => {
  return request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(data),
  })
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return request('/api/auth/me')
}

// 获取期刊列表
export const getJournals = (params = {}) => {
  const query = new URLSearchParams(params).toString()
  return request(`/api/journals?${query}`)
}

// 获取期刊分类
export const getCategories = () => {
  return request('/api/journals/categories')
}

export default request