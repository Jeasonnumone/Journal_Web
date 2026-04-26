import { ref } from 'vue'
import { getCurrentUser, refreshToken as apiRefreshToken, logout as apiLogout } from '../api/index.js'

const currentUser = ref(null)
let refreshTimer = null

// 刷新 Token
export async function refreshTokens() {
  try {
    const { data } = await apiRefreshToken()
    const { accessToken, accessTokenExpiresIn } = data.data
    
    localStorage.setItem('accessToken', accessToken)
    // console.log("expiresIn=", accessTokenExpiresIn)
    scheduleTokenRefresh(accessTokenExpiresIn)
  } catch (error) {
    console.error('刷新 Token 失败,退出登录:', error)
    logout()
  }
}

// 安排 Token 刷新
function scheduleTokenRefresh(expiresIn) {
  if (refreshTimer) clearTimeout(refreshTimer)

  if (!expiresIn || expiresIn <= 60) {
      refreshTimer = setTimeout(refreshTokens, 15*60*1000)
      console.log("定时任务开启(自定义设置),refreshTimer=", 15*60*1000)
      return
  }

  const refreshTime = (expiresIn - 60) * 1000

  refreshTimer = setTimeout(
      refreshTokens,
      refreshTime
  )
  console.log("定时任务开启,refreshTimer=", refreshTime)
}


// 初始化用户信息
export async function initUser() {
  const accessToken = localStorage.getItem('accessToken')
  if (!accessToken) return
  
  try {
    const { data } = await getCurrentUser()
    currentUser.value = data.data
    refreshTokens()
    // scheduleTokenRefresh()
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
