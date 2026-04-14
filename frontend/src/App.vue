<template>
  <div class="journal-app">
    <!-- 顶部搜索和分类 -->
    <header class="app-header">
      <h1>📚 德儒教育</h1>
      
      <!-- 搜索框 -->
      <div class="search-container">
        <input 
          v-model="keyword" 
          placeholder="搜索期刊..." 
          class="search-input"
          @input="search"
        />
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
          v-for="journal in paginatedJournals" 
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
      <div class="pagination" v-if="totalPages > 1">
        <button class="page-btn" @click="prevPage" :disabled="currentPage === 1">上一页</button>
        <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
        <button class="page-btn" @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
      </div>
      
      <!-- 无结果提示 -->
      <div class="no-results" v-if="paginatedJournals.length === 0">
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
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import natureImg from './assets/nature.jpg'
import scienceImg from './assets/science.jpg'

// 状态管理
const keyword = ref('')
const selectedCategory = ref('全部')
const selectedJournal = ref(null)
const currentPage = ref(1)
const itemsPerPage = ref(6)

// 期刊分类
const categories = ref([])

// 期刊数据
const journals = ref([])

// 后端API地址
const API_BASE_URL = 'http://localhost:8081'

// 获取期刊分类
const getCategories = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/journals/categories`)
    const result = await response.json()
    if (result.code === 200) {
      categories.value = result.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取期刊数据
const getJournals = async () => {
  try {
    const response = await fetch(`${API_BASE_URL}/api/journals?keyword=${keyword.value}&category=${selectedCategory.value}`)
    const result = await response.json()
    if (result.code === 200) {
      journals.value = result.data
    }
  } catch (error) {
    console.error('获取期刊失败:', error)
  }
}

// 搜索期刊
const search = async () => {
  currentPage.value = 1
  await getJournals()
}

// 选择分类
const selectCategory = async (category) => {
  selectedCategory.value = category
  currentPage.value = 1
  await getJournals()
}

// 显示期刊详情
const showDetails = (journal) => {
  selectedJournal.value = journal
}

// 返回列表
const backToGrid = () => {
  selectedJournal.value = null
}

// 分页相关计算
const paginatedJournals = computed(() => {
  const startIndex = (currentPage.value - 1) * itemsPerPage.value
  return journals.value.slice(startIndex, startIndex + itemsPerPage.value)
})

const totalPages = computed(() => {
  return Math.ceil(journals.value.length / itemsPerPage.value)
})

// 上一页
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
  }
}

// 下一页
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
  }
}

// 页面加载时初始化数据
onMounted(async () => {
  await getCategories()
  await getJournals()
})
</script>

<style scoped>
.journal-app {
  font-family: 'Arial', sans-serif;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.app-header {
  text-align: center;
  margin-bottom: 30px;
}

.app-header h1 {
  font-size: 36px;
  color: #333;
  margin-bottom: 20px;
}

.search-container {
  margin-bottom: 30px;
}

.search-input {
  width: 50%;
  padding: 10px;
  font-size: 16px;
  border: 2px solid #ddd;
  border-radius: 5px;
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
  border-radius: 5px;
  background-color: #fff;
  cursor: pointer;
  transition: all 0.3s;
}

.category-btn:hover {
  border-color: #aa3bff;
  color: #aa3bff;
}

.category-btn.active {
  background-color: #aa3bff;
  color: #fff;
  border-color: #aa3bff;
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
}

.journal-card:hover {
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  transform: translateY(-5px);
}

.journal-cover img {
  width: 100%;
  height: 300px;
  object-fit: cover;
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
}

.journal-details {
  max-width: 800px;
  margin: 0 auto;
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

.detail-cover img {
  width: 300px;
  height: 400px;
  object-fit: cover;
  border-radius: 10px;
}

.detail-info h2 {
  font-size: 28px;
  color: #333;
  margin-bottom: 20px;
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

  .detail-cover img {
    width: 100%;
    height: auto;
  }
}
</style>