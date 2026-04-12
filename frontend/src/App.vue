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
import { ref, computed } from 'vue'
import natureImg from './assets/nature.jpg'
import scienceImg from './assets/science.jpg'

// 状态管理
const keyword = ref('')
const selectedCategory = ref('全部')
const selectedJournal = ref(null)
const currentPage = ref(1)
const itemsPerPage = ref(6)

// 期刊分类
const categories = ['全部', '自然科学', '医学', '计算机', '物理', '化学', '经济', '环境']

// 期刊数据
const journals = ref([
  // 自然科学
  {
    id: 1,
    title: 'Nature',
    author: 'Nature Publishing Group',
    category: '自然科学',
    cover: natureImg,
    description: 'Nature是世界上最权威的科学期刊之一，涵盖生物学、物理学、化学等多个学科领域的原创研究。',
    publisher: 'Nature Publishing Group',
    issn: '0028-0836'
  },
  {
    id: 2,
    title: 'Science',
    author: 'American Association for the Advancement of Science',
    category: '自然科学',
    cover: scienceImg,
    description: 'Science是一本综合性科学期刊，发表各领域的重要研究成果和评论。',
    publisher: 'AAAS',
    issn: '0036-8075'
  },
  // 医学
  {
    id: 3,
    title: 'The New England Journal of Medicine',
    author: 'Massachusetts Medical Society',
    category: '医学',
    cover: 'https://via.placeholder.com/200x280/ef4444/ffffff?text=NEJM',
    description: 'NEJM是世界上最古老、最受尊敬的医学期刊之一，发表医学领域的原创研究和临床实践。',
    publisher: 'Massachusetts Medical Society',
    issn: '0028-4793'
  },
  {
    id: 4,
    title: 'The Lancet',
    author: 'Elsevier',
    category: '医学',
    cover: 'https://via.placeholder.com/200x280/f97316/ffffff?text=Lancet',
    description: 'The Lancet是全球最权威的医学期刊之一，发表医学研究、评论和观点。',
    publisher: 'Elsevier',
    issn: '0140-6736'
  },
  // 计算机
  {
    id: 5,
    title: 'Communications of the ACM',
    author: 'Association for Computing Machinery',
    category: '计算机',
    cover: 'https://via.placeholder.com/200x280/8b5cf6/ffffff?text=ACM',
    description: 'CACM是计算机科学领域的权威期刊，发表计算机理论、实践和应用方面的研究。',
    publisher: 'Association for Computing Machinery',
    issn: '0001-0782'
  },
  {
    id: 6,
    title: 'IEEE Transactions on Computers',
    author: 'IEEE Computer Society',
    category: '计算机',
    cover: 'https://via.placeholder.com/200x280/14b8a6/ffffff?text=IEEE',
    description: 'IEEE Transactions on Computers是计算机工程领域的顶级期刊，发表计算机硬件和软件方面的研究。',
    publisher: 'IEEE Computer Society',
    issn: '0018-9340'
  },
  // 物理
  {
    id: 7,
    title: 'Physical Review Letters',
    author: 'American Physical Society',
    category: '物理',
    cover: 'https://via.placeholder.com/200x280/3b82f6/ffffff?text=PRL',
    description: 'Physical Review Letters是物理学领域的顶级期刊，发表物理学各分支的重要研究成果。',
    publisher: 'American Physical Society',
    issn: '0031-9007'
  },
  {
    id: 8,
    title: 'Nature Physics',
    author: 'Nature Publishing Group',
    category: '物理',
    cover: 'https://via.placeholder.com/200x280/8b5cf6/ffffff?text=Nat+Phys',
    description: 'Nature Physics是物理学领域的权威期刊，发表物理学前沿研究成果。',
    publisher: 'Nature Publishing Group',
    issn: '1745-2473'
  },
  // 化学
  {
    id: 9,
    title: 'Journal of the American Chemical Society',
    author: 'American Chemical Society',
    category: '化学',
    cover: 'https://via.placeholder.com/200x280/ec4899/ffffff?text=JACS',
    description: 'JACS是化学领域的顶级期刊，发表化学各分支的原创研究。',
    publisher: 'American Chemical Society',
    issn: '0002-7863'
  },
  {
    id: 10,
    title: 'Angewandte Chemie',
    author: 'Wiley-VCH',
    category: '化学',
    cover: 'https://via.placeholder.com/200x280/f59e0b/ffffff?text=Angew',
    description: 'Angewandte Chemie是化学领域的权威期刊，发表化学研究的重要成果。',
    publisher: 'Wiley-VCH',
    issn: '1433-7851'
  },
  // 经济
  {
    id: 11,
    title: 'The Quarterly Journal of Economics',
    author: 'Oxford University Press',
    category: '经济',
    cover: 'https://via.placeholder.com/200x280/10b981/ffffff?text=QJE',
    description: 'QJE是经济学领域的顶级期刊，发表经济学理论和实证研究。',
    publisher: 'Oxford University Press',
    issn: '0033-5533'
  },
  {
    id: 12,
    title: 'Journal of Political Economy',
    author: 'University of Chicago Press',
    category: '经济',
    cover: 'https://via.placeholder.com/200x280/f59e0b/ffffff?text=JPE',
    description: 'JPE是经济学领域的权威期刊，发表经济学理论和政策研究。',
    publisher: 'University of Chicago Press',
    issn: '0022-3808'
  },
  // 环境
  {
    id: 13,
    title: 'Environmental Science & Technology',
    author: 'American Chemical Society',
    category: '环境',
    cover: 'https://via.placeholder.com/200x280/10b981/ffffff?text=EST',
    description: 'EST是环境科学领域的顶级期刊，发表环境科学和技术方面的研究。',
    publisher: 'American Chemical Society',
    issn: '0013-936X'
  },
  {
    id: 14,
    title: 'Nature Sustainability',
    author: 'Nature Publishing Group',
    category: '环境',
    cover: 'https://via.placeholder.com/200x280/22c55e/ffffff?text=Nat+Sust',
    description: 'Nature Sustainability是环境可持续性领域的权威期刊，发表可持续发展相关研究。',
    publisher: 'Nature Publishing Group',
    issn: '2398-9629'
  }
])

// 过滤后的期刊
const filteredJournals = computed(() => {
  return journals.value.filter(journal => {
    const matchesKeyword = journal.title.toLowerCase().includes(keyword.value.toLowerCase())
    const matchesCategory = selectedCategory.value === '全部' || journal.category === selectedCategory.value
    return matchesKeyword && matchesCategory
  })
})

// 分页后的期刊
const paginatedJournals = computed(() => {
  const startIndex = (currentPage.value - 1) * itemsPerPage.value
  const endIndex = startIndex + itemsPerPage.value
  return filteredJournals.value.slice(startIndex, endIndex)
})

// 总页数
const totalPages = computed(() => {
  return Math.ceil(filteredJournals.value.length / itemsPerPage.value)
})

// 搜索函数
const search = () => {
  // 搜索逻辑由computed自动处理
  currentPage.value = 1 // 搜索时重置到第一页
}

// 选择分类
const selectCategory = (category) => {
  selectedCategory.value = category
  currentPage.value = 1 // 切换分类时重置到第一页
}

// 显示详情
const showDetails = (journal) => {
  selectedJournal.value = journal
}

// 返回列表
const backToGrid = () => {
  selectedJournal.value = null
}

// 切换到指定页面
const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

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
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f5f5;
  color: #333;
}

.journal-app {
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
  margin-bottom: 20px;
  color: #1f2937;
}

.search-container {
  margin-bottom: 20px;
}

.search-input {
  width: 400px;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #3b82f6;
}

.category-container {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
  margin-bottom: 30px;
}

.category-btn {
  padding: 8px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 20px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.category-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.category-btn.active {
  background: #3b82f6;
  color: white;
  border-color: #3b82f6;
}

.journal-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.journal-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.journal-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1);
}

.journal-cover {
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.journal-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.journal-info {
  padding: 16px;
}

.journal-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1f2937;
}

.journal-author {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 4px;
}

.journal-category {
  font-size: 12px;
  color: #9ca3af;
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 10px;
  display: inline-block;
  margin-top: 8px;
}

/* 详情页样式 */
.journal-details {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.back-btn {
  padding: 10px 20px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  margin-bottom: 20px;
  transition: all 0.3s;
  font-size: 14px;
}

.back-btn:hover {
  border-color: #3b82f6;
  color: #3b82f6;
}

.detail-content {
  display: flex;
  gap: 30px;
}

.detail-cover {
  flex: 0 0 200px;
  height: 280px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.detail-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.detail-info {
  flex: 1;
}

.detail-info h2 {
  font-size: 28px;
  margin-bottom: 16px;
  color: #1f2937;
}

.detail-author {
  font-size: 16px;
  color: #6b7280;
  margin-bottom: 8px;
}

.detail-category {
  font-size: 14px;
  color: #3b82f6;
  margin-bottom: 16px;
}

.detail-description {
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 20px;
  color: #4b5563;
}

.detail-publisher, .detail-issn {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

/* 分页控件样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 30px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
}

.page-btn {
  padding: 10px 20px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.page-btn:hover:not(:disabled) {
  border-color: #3b82f6;
  color: #3b82f6;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

/* 无结果提示 */
.no-results {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);
  margin-top: 20px;
}

.no-results p {
  font-size: 16px;
  color: #6b7280;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-input {
    width: 100%;
    max-width: 400px;
  }
  
  .journal-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 15px;
  }
  
  .detail-content {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .detail-cover {
    flex: none;
    width: 200px;
  }
  
  .pagination {
    flex-direction: column;
    gap: 10px;
  }
}
</style>