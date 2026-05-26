<template>
  <main class="comments-page">
    <div class="page-header">
      <el-button text @click="router.push('/')" class="back-btn">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h1 class="page-title">期刊点评</h1>
      <span class="placeholder"></span>
    </div>

    <div class="comments-container">
      <div v-if="loading" class="loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="comments.length === 0" class="empty">
        <el-empty description="暂无评论" />
      </div>

      <div v-else class="comment-list">
        <div 
          v-for="comment in comments" 
          :key="comment.id" 
          class="comment-item"
          @click="goToJournal(comment.journalId)"
        >
          <el-avatar :size="48" :src="comment.userId === currentUser?.id ? currentUser.avatar : null" :icon="UserFilled" class="comment-avatar" />
          <div class="comment-body">
            <div class="comment-header">
              <span class="comment-user">{{ comment.username || '匿名用户' }}</span>
              <span class="comment-action">评论了</span>
              <span class="comment-journal-name">{{ comment.journalTitle || '未知期刊' }}</span>
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
import { getRecentComments } from '../api/index.js'
import { currentUser } from '../composables/useAuth.js'
import { UserFilled, ArrowLeft, Loading } from '@element-plus/icons-vue'

const router = useRouter()

const comments = ref([])
const loading = ref(true)

const fetchComments = async () => {
  try {
    const { data } = await getRecentComments(50)
    comments.value = data.data || []
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    loading.value = false
  }
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

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comments-page {
  flex: 1;
  padding: 2rem;
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
}

.back-btn {
  color: #909399;
}

.placeholder {
  width: 60px;
}

.page-title {
  margin: 0;
  font-size: 1.5rem;
  color: #613ce6;
  text-align: center;
}

.comments-container {
  background: #fff;
  border-radius: 10px;
  padding: 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 3rem;
  color: #909399;
}

.loading .el-icon {
  font-size: 1.5rem;
}

.empty {
  padding: 3rem;
}

.comment-list {
  display: flex;
  flex-direction: column;
}

.comment-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.comment-item:last-child {
  border-bottom: none;
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

.comment-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
  flex-wrap: wrap;
}

.comment-user {
  font-weight: 600;
  color: #409eff;
}

.comment-action {
  color: #909399;
}

.comment-journal-name {
  color: #67c23a;
  font-weight: 500;
}

.comment-time {
  color: #909399;
  font-size: 0.85rem;
  margin-left: auto;
}

.comment-content {
  color: #606266;
  font-size: 0.95rem;
  line-height: 1.6;
}
</style>