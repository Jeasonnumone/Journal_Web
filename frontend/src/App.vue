<template>
  <div class="journal-app">
    <!-- 登录页面 -->
    <LoginPage 
      v-if="currentPage === 'login'" 
      @login-success="handleLoginSuccess" 
      @go-to-register="currentPage = 'register'"
    />
    
    <!-- 注册页面 -->
    <RegisterPage 
      v-else-if="currentPage === 'register'" 
      @register-success="handleRegisterSuccess" 
      @go-to-login="currentPage = 'login'"
    />
    
    <!-- 主页面 -->
    <div v-else>
      <!-- 顶部导航栏 -->
      <header class="app-header">
        <div class="header-top">
          <h1>📚 德儒教育</h1>
          <div class="user-info">
            <span class="username">{{ currentUser?.username }}</span>
            <button class="logout-btn" @click="handleLogout">退出</button>
          </div>
        </div>
        
        <!-- 搜索框 -->
        <div class="search-container">
          <input 
            v-model="keyword" 
            placeholder="搜索期刊..." 
            class="search-input"
          />
          <button class="search-btn" @click="search">🔍</button>
        </div>
        
        <!-- 期刊分类 -->
        <div class="category-container">
          <button 
            v-for="category in categories" 
            :key="category"
            class="category-btn"
            :class="{ active: selectedCategory === category }"
            @click="selectCategory(category)"
          >
            {{ category }}
          </button>
        </div>
      </header>
      
      <!-- 期刊卡片展示 -->
      <main v-if="!selectedJournal">
        <div class="journal-grid">
          <div 
            v-for="journal in journals" 
            :key="journal.id" 
            class="journal-card"
            @click="showDetails(journal)"
          >
            <div class="journal-cover">
              <img :src="journal.cover" :alt="journal.title" />
            </div>
            <div class="journal-info">
              <h3 class="journal-title">{{ journal.title }}</h3>
              <p class="journal-author">作者：{{ journal.author }}</p>
              <p class="journal-category">{{ journal.category }}</p>
            </div>
          </div>
        </div>
        
        <!-- 分页控件 -->
        <div class="pagination" v-if="totalPages > 1 || currentPage > 1">
          <button class="page-btn" @click="prevPage" :disabled="currentPage === 1">上一页</button>
          <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
          <button class="page-btn" @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
        </div>
        
        <!-- 无结果提示 -->
        <div class="no-results" v-if="journals.length === 0 && total === 0">
          <p>未找到相关期刊</p>
        </div>
      </main>
      
      <!-- 期刊详情页 -->
      <div class="journal-details" v-else>
        <button class="back-btn" @click="backToGrid">← 返回列表</button>
        <div class="detail-content">
          <div class="detail-cover">
            <img :src="selectedJournal.cover" :alt="selectedJournal.title" />
          </div>
          <div class="detail-info">
            <h2>{{ selectedJournal.title }}</h2>
            <p class="detail-author">作者：{{ selectedJournal.author }}</p>
            <p class="detail-category">分类：{{ selectedJournal.category }}</p>
            <p class="detail-description">{{ selectedJournal.description }}</p>
            <p class="detail-publisher">出版社：{{ selectedJournal.publisher }}</p>
            <p class="detail-issn">ISSN：{{ selectedJournal.issn }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import LoginPage from './views/LoginPage.vue'
import RegisterPage from './views/RegisterPage.vue'
import { getCategories, getJournals, getCurrentUser } from './api/index.js'

// 页面状态
const currentPage = ref('login')

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

// 登录成功处理
const handleLoginSuccess = async (user) => {
  currentUser.value = user
  currentPage.value = 'home'
  await fetchCategories()
  await fetchJournals()
}

// 注册成功处理
const handleRegisterSuccess = () => {
  currentPage.value = 'login'
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  currentUser.value = null
  currentPage.value = 'login'
}

// 获取期刊分类
const fetchCategories = async () => {
  try {
    const result = await getCategories()
    if (result.code === 200) {
      categories.value = result.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取期刊数据
const fetchJournals = async () => {
  try {
    const result = await getJournals({
      keyword: keyword.value,
      category: selectedCategory.value,
      page: pageNum.value,
      size: itemsPerPage.value
    })
    if (result.code === 200) {
      const pageData = result.data
      journals.value = pageData.records
      total.value = pageData.total
      totalPages.value = Math.ceil(pageData.total / pageData.size)
    }
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
}

// 返回列表
const backToGrid = () => {
  selectedJournal.value = null
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

// 页面加载时检查登录状态
onMounted(async () => {
  const token = localStorage.getItem('token')
  
  if (token) {
    try {
      const result = await getCurrentUser()
      if (result.code === 200) {
        currentUser.value = result.data
        localStorage.setItem('user', JSON.stringify(result.data))
        currentPage.value = 'home'
        await fetchCategories()
        await fetchJournals()
      } else {
        // token无效，清除本地存储
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
    } catch (error) {
      console.error('获取用户信息失败:', error)
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
  }
})
</script>

<style scoped>
.journal-app {
  font-family: 'Arial', sans-serif;
  width: 100%;
  margin: 0;
  padding: 20px;
  background-color: #f5f5f5;
  min-height: 100vh;
}

.app-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px;
  background-color: #fff;
  border-radius: 30px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-top h1 {
  font-size: 36px;
  color: #333;
  margin: 0;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 16px;
  color: #666;
}

.logout-btn {
  padding: 8px 16px;
  font-size: 14px;
  border: 2px solid #e74c3c;
  border-radius: 5px;
  background-color: #fff;
  color: #e74c3c;
  cursor: pointer;
  transition: all 0.3s;
}

.logout-btn:hover {
  background-color: #e74c3c;
  color: #fff;
}

.search-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-bottom: 30px;
}

.search-input {
  width: 50%;
  padding: 10px;
  font-size: 16px;
  border: 2px solid #ddd;
  border-radius: 5px;
  outline: none;
  transition: all 0.3s;
}

.search-input:focus {
  border-color: #aa3bff;
  box-shadow: 0 0 0 3px rgba(170, 59, 255, 0.1);
}

.search-btn {
  padding: 10px 20px;
  font-size: 16px;
  border: 2px solid #ddd;
  border-radius: 5px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover {
  border-color: #aa3bff;
  color: #aa3bff;
}

.category-container {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
}

.category-btn {
  padding: 10px 20px;
  font-size: 14px;
  border: 2px solid #ddd;
  border-radius: 20px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: bold;
}

.category-btn:hover {
  border-color: #aa3bff;
  color: #aa3bff;
  transform: translateY(-2px);
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.category-btn.active {
  background-color: #aa3bff;
  color: #fff;
  border-color: #aa3bff;
  box-shadow: 0 2px 5px rgba(170, 59, 255, 0.3);
}

.journal-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}

.journal-card {
  border: 2px solid #ddd;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.journal-card:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.journal-cover {
  width: 100%;
  height: 300px;
  overflow: hidden;
  background-color: #f5f5f5;
}

.journal-cover img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 20px;
  box-sizing: border-box;
}

.journal-info {
  padding: 15px;
}

.journal-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.journal-author {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.journal-category {
  font-size: 14px;
  color: #aa3bff;
  font-weight: bold;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
}

.page-btn {
  padding: 10px 20px;
  font-size: 14px;
  border: 2px solid #ddd;
  border-radius: 5px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.page-btn:hover:not(:disabled) {
  border-color: #aa3bff;
  color: #aa3bff;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}

.no-results {
  text-align: center;
  font-size: 18px;
  color: #666;
  padding: 50px 0;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.journal-details {
  max-width: 800px;
  margin: 0 auto;
  background-color: #fff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.back-btn {
  padding: 10px 20px;
  font-size: 14px;
  border: 2px solid #ddd;
  border-radius: 5px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 20px;
}

.back-btn:hover {
  border-color: #aa3bff;
  color: #aa3bff;
}

.detail-content {
  display: flex;
  gap: 30px;
}

.detail-cover {
  width: 300px;
  height: 400px;
  overflow: hidden;
  border-radius: 10px;
  background-color: #f5f5f5;
}

.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  padding: 20px;
  box-sizing: border-box;
}

.detail-info h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
  font-weight: bold;
}

.detail-author {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.detail-category {
  font-size: 16px;
  color: #aa3bff;
  font-weight: bold;
  margin-bottom: 20px;
}

.detail-description {
  font-size: 16px;
  color: #333;
  line-height: 1.6;
  margin-bottom: 20px;
}

.detail-publisher {
  font-size: 16px;
  color: #666;
  margin-bottom: 10px;
}

.detail-issn {
  font-size: 16px;
  color: #666;
}

@media (max-width: 768px) {
  .detail-content {
    flex-direction: column;
  }

  .detail-cover {
    width: 100%;
    height: auto;
  }

  .detail-cover img {
    width: 100%;
    height: auto;
  }
}
</style>