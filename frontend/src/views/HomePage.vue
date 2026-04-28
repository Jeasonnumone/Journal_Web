<template>
  <main class="main-content">
    <div class="search-bar">
      <el-input v-model="keyword" @keyup.enter="search" placeholder="搜索期刊..." class="search-input" />
      <el-button type="primary" @click="search">搜索</el-button>
    </div>

    <div class="categories">
      <el-button 
        v-for="category in categories" 
        :key="category"
        @click="selectCategory(category)"
        :type="selectedCategory === category ? 'primary' : 'default'"
        round
      >
        {{ category }}
      </el-button>
    </div>

    <div class="journal-grid">
      <el-card 
        v-for="journal in journals" 
        :key="journal.id"
        @click="viewDetail(journal.id)"
        class="journal-card"
        shadow="hover"
      >
        <img :src="journal.cover || '/default-cover.jpg'" alt="封面" class="journal-cover" />
        <h3 class="journal-title">{{ journal.title }}</h3>
        <p class="journal-author">作者：{{ journal.author }}</p>
      </el-card>
    </div>

    <div v-if="totalPages > 1" class="pagination">
      <el-pagination
        v-model:current-page="pageNum"
        layout="prev, pager, next"
        :total="total"
        :page-size="itemsPerPage"
        @current-change="fetchJournals"
      />
    </div>

    <div class="recent-comments">
      <div class="section-header">
        <el-icon><ChatDotRound /></el-icon>
        <span class="section-title">最新评论</span>
      </div>
      
      <div v-if="recentComments.length === 0" class="empty-comments">
        <el-empty description="暂无评论" />
      </div>
      
      <div v-else class="comment-list">
        <div 
          v-for="comment in recentComments" 
          :key="comment.id" 
          class="comment-item"
          @click="goToJournal(comment.journalId)"
        >
          <el-avatar :size="32" :icon="UserFilled" class="comment-avatar" />
          <div class="comment-body">
            <div class="comment-info">
              <span class="comment-user">{{ comment.username || '匿名用户' }}</span>
              <span class="comment-journal">评论了 {{ comment.journalTitle || '未知期刊' }}</span>
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
          </div>
        </div>
      </div>
    </div>
  </main>

</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, getJournals, getRecentComments } from '../api/index.js'
import { ChatDotRound, UserFilled } from '@element-plus/icons-vue'

const router = useRouter()

const keyword = ref('')
const selectedCategory = ref('全部')
const categories = ref([])
const journals = ref([])
const pageNum = ref(1)
const itemsPerPage = ref(6)
const total = ref(0)
const totalPages = ref(0)
const recentComments = ref([])

const fetchCategories = async () => {
  try {
    const { data } = await getCategories()
    categories.value = data.data
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchJournals = async () => {
  try {
    const { data } = await getJournals({
      keyword: keyword.value,
      category: selectedCategory.value,
      page: pageNum.value,
      size: itemsPerPage.value
    })
    const pageData = data.data
    journals.value = pageData.records
    total.value = pageData.total
    totalPages.value = Math.ceil(pageData.total / pageData.size)
  } catch (error) {
    console.error('获取期刊失败:', error)
  }
}

const fetchRecentComments = async () => {
  try {
    const { data } = await getRecentComments(10)
    recentComments.value = data.data || []
  } catch (error) {
    console.error('获取最新评论失败:', error)
  }
}

const search = () => {
  pageNum.value = 1
  fetchJournals()
}

const selectCategory = (category) => {
  selectedCategory.value = category
  pageNum.value = 1
  fetchJournals()
}

const viewDetail = (id) => {
  router.push(`/journal/${id}`)
}

const goToJournal = (id) => {
  router.push(`/journal/${id}`)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)} 天前`
  
  return date.toLocaleDateString('zh-CN')
}

onMounted(async () => {
  await fetchCategories()
  await fetchJournals()
  await fetchRecentComments()
})
</script>

<style scoped>
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
}

.categories {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.journal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.journal-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.journal-card:hover {
  transform: translateY(-5px);
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
}

.journal-author {
  color: #666;
  font-size: 0.85rem;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}

.recent-comments {
  margin-top: 3rem;
  background: #fff;
  border-radius: 10px;
  padding: 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #eee;
}

.section-header .el-icon {
  font-size: 1.25rem;
  color: #409eff;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.empty-comments {
  padding: 2rem 0;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.comment-item:hover {
  background-color: #f5f7fa;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
  min-width: 0;
}

.comment-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  font-size: 0.85rem;
}

.comment-user {
  font-weight: 500;
  color: #409eff;
}

.comment-journal {
  color: #909399;
}

.comment-time {
  color: #c0c4cc;
  margin-left: auto;
}

.comment-content {
  color: #606266;
  font-size: 0.9rem;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
