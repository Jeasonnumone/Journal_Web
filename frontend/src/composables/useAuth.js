import { ref } from 'vue'
import { getCurrentUser, refreshToken as apiRefreshToken, logout as apiLogout } from '../api/index.js'

const currentUser = ref(null)
let refreshTimer = null

// 刷新 Token
export async function refreshTokens() {
  try {
    const { data } = await apiRefreshToken()
    const { accessToken, accessTokenExpiresIn } = data
    
    localStorage.setItem('accessToken', accessToken)
    scheduleTokenRefresh(accessTokenExpiresIn)
  } catch (error) {
    logout()
  }
}

// 安排 Token 刷新
function scheduleTokenRefresh(expiresIn) {
  if (refreshTimer) clearTimeout(refreshTimer)
  
  const refreshTime = (expiresIn - 120) * 1000
  if (refreshTime > 1000) {
    refreshTimer = setTimeout(refreshTokens, refreshTime)
  } else {
    refreshTokens()
  }
}

// 初始化用户信息
export async function initUser() {
  const accessToken = localStorage.getItem('accessToken')
  if (!accessToken) return
  
  try {
    const { data } = await getCurrentUser()
    currentUser.value = data.data
    scheduleTokenRefresh()
  } catch (error) {
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }
}

// 登录成功处理
export function handleLoginSuccess(user, accessTokenExpiresIn) {
  currentUser.value = user
  if (accessTokenExpiresIn) scheduleTokenRefresh(accessTokenExpiresIn)
}

// 退出登录
export async function logout() {
  try {
    await apiLogout()
  } catch (error) {
    console.error('退出登录失败:', error)
  } finally {
    localStorage.removeItem('accessToken')
    if (refreshTimer) clearTimeout(refreshTimer)
    currentUser.value = null
  }
}

export { currentUser }
