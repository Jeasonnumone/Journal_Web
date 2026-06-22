import apiClient from './index.js'

// ==================== 统计 ====================
export const getAdminStats = () => {
  return apiClient.get('/api/admin/stats')
}

// ==================== 用户管理 ====================
export const getAdminUsers = (params = {}) => {
  return apiClient.get('/api/admin/users', { params })
}

export const updateUserRole = (id, role) => {
  return apiClient.put(`/api/admin/users/${id}/role`, { role })
}

export const deleteUser = (id) => {
  return apiClient.delete(`/api/admin/users/${id}`)
}

// ==================== 期刊管理 ====================
export const getAdminJournals = (params = {}) => {
  return apiClient.get('/api/admin/journals', { params })
}

export const createJournal = (data) => {
  return apiClient.post('/api/admin/journals', data)
}

export const updateJournal = (id, data) => {
  return apiClient.put(`/api/admin/journals/${id}`, data)
}

export const deleteJournal = (id) => {
  return apiClient.delete(`/api/admin/journals/${id}`)
}

export const batchReplaceJournals = (data) => {
  return apiClient.post('/api/admin/journals/batch-replace', data)
}

// ==================== 评论管理 ====================
export const getAdminComments = (params = {}) => {
  return apiClient.get('/api/admin/comments', { params })
}

export const adminDeleteComment = (id) => {
  return apiClient.delete(`/api/admin/comments/${id}`)
}

// ==================== 帖子管理 ====================
export const getAdminPosts = (params = {}) => {
  return apiClient.get('/api/admin/posts', { params })
}

export const adminDeletePost = (id) => {
  return apiClient.delete(`/api/admin/posts/${id}`)
}