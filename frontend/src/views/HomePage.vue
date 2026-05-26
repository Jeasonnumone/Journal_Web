<template>
  <main class="main-content">
    <div class="search-bar">
      <el-input v-model="keyword" @keyup.enter="search" placeholder="搜索期刊..." class="search-input" />
      <el-button type="primary" @click="search">搜索</el-button>
    </div>

    <div class="categories">
      <el-button 
        v-for="category in displayedCategories" 
        :key="category.typeid"
        @click="selectCategory(category)"
        :type="selectedTypeid === category.typeid ? 'primary' : 'default'"
        round
      >
        {{ category.name }}
      </el-button>
      <el-button 
        v-if="categories.length > maxVisibleCategories"
        @click="router.push('/categories')"
        type="info"
        plain
        round
      >
        更多 ▼
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
        <img :src="journal.coverPath || '/default-cover.jpg'" alt="封面" class="journal-cover" />
        <h3 class="journal-title">{{ truncateTitle(journal.title) }}</h3>
        <p class="journal-organizer">{{ journal.organizer || '未知主办单位' }}</p>
        <div class="journal-tags" v-if="journal.compositeImpactFactor">
          <el-tag size="small" type="success">影响因子：{{ journal.compositeImpactFactor }}</el-tag>
        </div>
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

    <div class="recent-posts">
      <div class="section-header">
        <el-icon><Document /></el-icon>
        <span class="section-title">最新帖子</span>
        <el-button type="primary" size="small" round @click="router.push('/posts/publish')" v-if="currentUser">
          发表帖子
        </el-button>
      </div>
      
      <div v-if="recentPosts.length === 0" class="empty-posts">
        <el-empty description="暂无帖子，快来发表第一篇吧！" />
      </div>
      
      <div v-else class="post-list">
        <div 
          v-for="post in recentPosts" 
          :key="post.id" 
          class="post-item"
          @click="goToPost(post.id)"
        >
          <el-avatar :size="36" :src="post.avatar || null" :icon="UserFilled" class="post-avatar" />
          <div class="post-body">
            <div class="post-header">
              <h3 class="post-title">{{ post.title }}</h3>
              <div class="post-meta">
                <span class="post-user">{{ post.username || '匿名用户' }}</span>
                <span class="post-time">{{ formatTime(post.createTime) }}</span>
                <span class="post-views">👁 {{ post.viewCount || 0 }} 浏览</span>
              </div>
            </div>
            <div class="post-content">{{ post.content }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="recent-comments">
      <div class="section-header">
        <el-icon><ChatDotRound /></el-icon>
        <span class="section-title">最新评论</span>
        <el-button type="primary" size="small" round @click="router.push('/comments')">
          查看更多
        </el-button>
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
          <el-avatar :size="32" :src="comment.avatar || null" :icon="UserFilled" class="comment-avatar" />
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
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getCategories, getJournals, getRecentComments, getRecentPosts } from '../api/index.js'
import { ChatDotRound, UserFilled, Document } from '@element-plus/icons-vue'
import { currentUser } from '../composables/useAuth.js'

const router = useRouter()
const route = useRoute()

const keyword = ref('')
const selectedTypeid = ref(0)
const categories = ref([])
const journals = ref([])
const pageNum = ref(1)
const itemsPerPage = ref(10)
const total = ref(0)
const totalPages = ref(0)
const recentComments = ref([])
const recentPosts = ref([])
const maxVisibleCategories = 9

const displayedCategories = computed(() => {
  const filtered = categories.value.filter(c => c.parentId !== null)
  return filtered.slice(0, maxVisibleCategories)
})

const fetchCategories = async () => {
  try {
    const { data } = await getCategories()
    const allCategory = { typeid: 0, name: '全部' }
    categories.value = [allCategory, ...(data.data || [])]
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchJournals = async () => {
  try {
    const params = {
      keyword: keyword.value,
      page: pageNum.value,
      size: itemsPerPage.value
    }
    if (selectedTypeid.value > 0) {
      params.typeid = selectedTypeid.value
    }
    const { data } = await getJournals(params)
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

const fetchRecentPosts = async () => {
  try {
    const { data } = await getRecentPosts(1, 5)
    recentPosts.value = data.data?.records || []
  } catch (error) {
    console.error('获取最新帖子失败:', error)
  }
}

const search = () => {
  pageNum.value = 1
  fetchJournals()
}

const selectCategory = (category) => {
  selectedTypeid.value = category.typeid
  pageNum.value = 1
  fetchJournals()
}

const viewDetail = (id) => {
  router.push(`/journal/${id}`)
}

const goToJournal = (id) => {
  router.push(`/journal/${id}`)
}

const goToPost = (id) => {
  router.push(`/posts/${id}`)
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

const truncateTitle = (title) => {
  if (!title) return ''
  const index = title.indexOf('（')
  if (index > 0) {
    return title.substring(0, index)
  }
  return title
}

onMounted(async () => {
  if (route.query.typeid) {
    selectedTypeid.value = parseInt(route.query.typeid)
  }
  await fetchCategories()
  await fetchJournals()
  await fetchRecentComments()
  await fetchRecentPosts()
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
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
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
  height: auto;
  object-fit: cover;
  border-radius: 5px;
  margin-bottom: 1rem;
}

.journal-title {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
}

.journal-organizer {
  color: #666;
  font-size: 0.85rem;
  margin: 0;
}

.journal-tags {
  margin-top: 0.5rem;
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
  color: #606266;
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

.recent-posts {
  margin-top: 3rem;
  background: #fff;
  border-radius: 10px;
  padding: 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.empty-posts {
  padding: 2rem 0;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.post-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.post-item:hover {
  background-color: #f5f7fa;
}

.post-avatar {
  flex-shrink: 0;
}

.post-body {
  flex: 1;
  min-width: 0;
}

.post-header {
  margin-bottom: 0.5rem;
}

.post-title {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #333;
  font-weight: 600;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 0.8rem;
}

.post-user {
  font-weight: 500;
  color: #409eff;
}

.post-time {
  color: #c0c4cc;
}

.post-views {
  color: #909399;
}

.post-content {
  color: #606266;
  font-size: 0.9rem;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}
</style>