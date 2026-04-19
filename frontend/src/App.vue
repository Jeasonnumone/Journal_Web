<template>
  <div id="app">
    <!-- 顶部导航栏 -->
    <header class="header" v-if="currentPage === 'home'">
      <div class="header-content">
        <h1 class="logo">📚 德儒教育期刊系统</h1>
        <div class="user-info">
          <template v-if="currentUser">
            <span class="welcome">欢迎，{{ currentUser.username }}</span>
            <button class="logout-btn" @click="handleLogout">退出登录</button>
          </template>
          <template v-else>
            <button class="login-btn" @click="currentPage = 'login'">登录</button>
            <button class="register-btn" @click="currentPage = 'register'">注册</button>
          </template>
        </div>
      </div>
    </header>

    <!-- 主页面 -->
    <main class="main-content" v-if="currentPage === 'home'">
      <div class="search-bar">
        <input 
          v-model="keyword" 
          @keyup.enter="search" 
          placeholder="搜索期刊..." 
          class="search-input" 
        />
        <button @click="search" class="search-btn">搜索</button>
      </div>

      <div class="categories">
        <button 
          v-for="category in categories" 
          :key="category"
          @click="selectCategory(category)"
          :class="{ active: selectedCategory === category }"
          class="category-btn"
        >
          {{ category }}
        </button>
      </div>

      <div class="journal-grid">
        <div 
          v-for="journal in journals" 
          :key="journal.id"
          @click="showDetails(journal)"
          class="journal-card"
        >
          <img :src="journal.cover || '/default-cover.jpg'" alt="封面" class="journal-cover" />
          <h3 class="journal-title">{{ journal.title }}</h3>
          <p class="journal-author">作者：{{ journal.author }}</p>
        </div>
      </div>

      <div class="pagination" v-if="totalPages > 1">
        <button @click="prevPage" :disabled="pageNum === 1" class="page-btn">上一页</button>
        <span class="page-info">第 {{ pageNum }} / {{ totalPages }} 页</span>
        <button @click="nextPage" :disabled="pageNum === totalPages" class="page-btn">下一页</button>
      </div>
    </main>

    <!-- 期刊详情页面 -->
    <main class="detail-page" v-if="currentPage === 'detail'">
      <div class="detail-container">
        <button class="back-btn" @click="backToGrid">← 返回列表</button>
        <div class="detail-body">
          <div class="detail-left">
            <img :src="selectedJournal.cover || '/default-cover.jpg'" alt="封面" class="detail-cover" />
          </div>
          <div class="detail-right">
            <h2 class="detail-title">{{ selectedJournal.title }}</h2>
            <div class="detail-info">
              <p><strong>作者：</strong>{{ selectedJournal.author }}</p>
              <p><strong>出版社：</strong>{{ selectedJournal.publisher }}</p>
              <p><strong>ISSN：</strong>{{ selectedJournal.issn }}</p>
            </div>
            <div class="detail-description">
              <h3>简介</h3>
              <p>{{ selectedJournal.description }}</p>
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- 登录页面 -->
    <LoginPage 
      v-if="currentPage === 'login'" 
      @login-success="handleLoginSuccess" 
      @show-register="currentPage = 'register'"
      @back-home="backToHome"
    />

    <!-- 注册页面 -->
    <RegisterPage 
      v-if="currentPage === 'register'" 
      @show-login="currentPage = 'login'"
      @back-home="backToHome"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import LoginPage from './views/LoginPage.vue'
import RegisterPage from './views/RegisterPage.vue'
import { getCategories, getJournals, getCurrentUser, refreshToken as apiRefreshToken, logout as apiLogout } from './api/index.js'

// 页面状态
const currentPage = ref('home')

// 用户信息
const currentUser = ref(null)

// 状态管理
const keyword = ref('')
const selectedCategory = ref('全部')
const selectedJournal = ref(null)
const pageNum = ref(1)
const itemsPerPage = ref(6)
const total = ref(0)

// 期刊分类
const categories = ref([])

// 期刊数据
const journals = ref([])

// 计算总页数
const totalPages = ref(0)

// Token 刷新定时器
let refreshTimer = null

// 刷新 Token
const refreshTokens = async () => {
  try {
    const response = await apiRefreshToken()
    localStorage.setItem('accessToken', response.data.data.accessToken)
    
    // 设置下一次刷新定时器（在 Access Token 过期前 2 分钟刷新）
    scheduleTokenRefresh()
  } catch (error) {
    console.error('刷新 Token 失败:', error)
    // 刷新失败，清除 token 并跳转到登录页
    localStorage.removeItem('accessToken')
    currentUser.value = null
    currentPage.value = 'login'
  }
}

// 安排 Token 刷新（Access Token 过期前 2 分钟）
const scheduleTokenRefresh = () => {
  if (refreshTimer) {
    clearTimeout(refreshTimer)
  }
  
  // Access Token 有效期 15 分钟，在 13 分钟后刷新
  refreshTimer = setTimeout(() => {
    refreshTokens()
  }, 13 * 60 * 1000)
}

// 登录成功处理
const handleLoginSuccess = async (user) => {
  currentUser.value = user
  currentPage.value = 'home'
  // 安排 Token 刷新
  scheduleTokenRefresh()
  await fetchCategories()
  await fetchJournals()
}

// 注册成功处理
const handleRegisterSuccess = () => {
  currentPage.value = 'login'
}

// 返回主页
const backToHome = () => {
  currentPage.value = 'home'
}

// 退出登录
const handleLogout = async () => {
  try {
    // 调用后端 logout 接口，清除 Cookie 和 Redis 中的 Refresh Token
    await apiLogout()
  } catch (error) {
    console.error('退出登录失败:', error)
  } finally {
    // 清除本地存储
    localStorage.removeItem('accessToken')
    if (refreshTimer) {
      clearTimeout(refreshTimer)
    }
    currentUser.value = null
    currentPage.value = 'home'
  }
}

// 获取期刊分类
const fetchCategories = async () => {
  try {
    const response = await getCategories()
    categories.value = response.data.data
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取期刊数据
const fetchJournals = async () => {
  try {
    const response = await getJournals({
      keyword: keyword.value,
      category: selectedCategory.value,
      page: pageNum.value,
      size: itemsPerPage.value
    })
    const pageData = response.data.data
    journals.value = pageData.records
    total.value = pageData.total
    totalPages.value = Math.ceil(pageData.total / pageData.size)
  } catch (error) {
    console.error('获取期刊失败:', error)
  }
}

// 搜索期刊
const search = async () => {
  pageNum.value = 1
  await fetchJournals()
}

// 选择分类
const selectCategory = async (category) => {
  selectedCategory.value = category
  pageNum.value = 1
  await fetchJournals()
}

// 显示期刊详情
const showDetails = (journal) => {
  selectedJournal.value = journal
  currentPage.value = 'detail'
}

// 返回列表
const backToGrid = () => {
  selectedJournal.value = null
  currentPage.value = 'home'
}

// 上一页
const prevPage = () => {
  if (pageNum.value > 1) {
    pageNum.value--
    fetchJournals()
  }
}

// 下一页
const nextPage = () => {
  if (pageNum.value < totalPages.value) {
    pageNum.value++
    fetchJournals()
  }
}

// 页面加载时检查登录状态并加载数据
onMounted(async () => {
  await fetchCategories()
  await fetchJournals()
  
  const accessToken = localStorage.getItem('accessToken')
  
  if (accessToken) {
    try {
      const response = await getCurrentUser()
      currentUser.value = response.data.data
      // 安排 Token 刷新
      scheduleTokenRefresh()
    } catch (error) {
      console.error('获取用户信息失败:', error)
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
    }
  }
})
</script>

<style scoped>
#app {
  font-family: Arial, sans-serif;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  margin: 0;
  font-size: 1.5rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.welcome {
  margin-right: 1rem;
}

.login-btn, .register-btn, .logout-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 0.5rem 1rem;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.login-btn:hover, .register-btn:hover, .logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.main-content {
  flex: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.search-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.search-input {
  flex: 1;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
}

.search-btn {
  padding: 0.75rem 2rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
}

.search-btn:hover {
  background: #5568d3;
}

.categories {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.category-btn {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-btn:hover {
  background: #f5f5f5;
}

.category-btn.active {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

.journal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.journal-card {
  background: white;
  border-radius: 10px;
  padding: 1rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.journal-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.journal-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 5px;
  margin-bottom: 1rem;
}

.journal-title {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
  font-weight: bold;
}

.journal-author {
  color: #666;
  font-size: 0.85rem;
  margin: 0.3rem 0;
}

.journal-category {
  color: #667eea;
  font-size: 0.85rem;
  margin: 0.3rem 0;
}

.journal-description {
  color: #666;
  font-size: 0.85rem;
  margin: 0.5rem 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.page-btn {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.page-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.page-info {
  color: #666;
}

/* 详情页面样式 */
.detail-page {
  flex: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  background: #f5f5f5;
  min-height: calc(100vh - 80px);
}

.detail-container {
  background: white;
  border-radius: 10px;
  padding: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.back-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
  margin-bottom: 2rem;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #5568d3;
}

.detail-body {
  display: flex;
  gap: 3rem;
}

.detail-left {
  flex: 0 0 350px;
}

.detail-cover {
  width: 100%;
  height: auto;
  border-radius: 5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.detail-right {
  flex: 1;
}

.detail-title {
  margin: 0 0 1.5rem 0;
  color: #333;
  font-size: 2rem;
  border-bottom: 3px solid #667eea;
  padding-bottom: 1rem;
}

.detail-info {
  margin-bottom: 2rem;
  background: #f9f9f9;
  padding: 1.5rem;
  border-radius: 8px;
}

.detail-info p {
  margin: 0.75rem 0;
  color: #555;
  font-size: 1rem;
  line-height: 1.6;
}

.detail-info strong {
  color: #333;
  margin-right: 0.5rem;
}

.detail-description {
  margin-top: 2rem;
}

.detail-description h3 {
  color: #333;
  font-size: 1.3rem;
  margin-bottom: 1rem;
  border-left: 4px solid #667eea;
  padding-left: 1rem;
}

.detail-description p {
  color: #666;
  font-size: 1rem;
  line-height: 1.8;
  text-align: justify;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-body {
    flex-direction: column;
  }
  
  .detail-left {
    flex: none;
    width: 100%;
  }
  
  .detail-cover {
    max-width: 100%;
  }
}
</style>
