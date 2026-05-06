<template>
  <div class="post-detail-page">
    <div class="post-container" v-if="post">
      <div class="post-header">
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-meta">
          <el-avatar :size="40" :icon="UserFilled" />
          <div class="meta-info">
            <span class="username">{{ post.username }}</span>
            <span class="time">{{ formatTime(post.createTime) }}</span>
          </div>
          <div class="post-stats">
            <span>👁 {{ post.viewCount }} 浏览</span>
          </div>
        </div>
      </div>

      <div class="post-content">
        {{ post.content }}
      </div>

      <div class="post-actions" v-if="isOwner">
        <el-button type="primary" @click="goToEdit">编辑</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </div>
    </div>

    <div v-else class="loading">
      <el-skeleton :rows="5" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostById, deletePost } from '../api/index.js'
import { UserFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { currentUser } from '../composables/useAuth.js'

const route = useRoute()
const router = useRouter()
const post = ref(null)
const isOwner = ref(false)

const fetchPost = async () => {
  try {
    const { data } = await getPostById(route.params.id)
    post.value = data.data
    
    isOwner.value = currentUser.value && post.value.userId === currentUser.value.id
  } catch (error) {
    console.error('获取帖子失败:', error)
  }
}

const goToEdit = () => {
  router.push(`/posts/${post.value.id}/edit`)
}

const handleDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这篇帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deletePost(post.value.id)
    ElMessage.success('删除成功')
    router.push('/')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  fetchPost()
})
</script>

<style scoped>
.post-detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 2rem;
}

.post-container {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  border-radius: 10px;
  padding: 2rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.post-header {
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
}

.post-title {
  margin: 0 0 1rem 0;
  font-size: 1.8rem;
  color: #333;
  font-weight: 600;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.meta-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.username {
  font-weight: 500;
  color: #409eff;
}

.time {
  font-size: 0.85rem;
  color: #909399;
}

.post-stats {
  color: #909399;
  font-size: 0.9rem;
}

.post-content {
  font-size: 1rem;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  margin-bottom: 2rem;
}

.post-actions {
  display: flex;
  gap: 1rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.loading {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  border-radius: 10px;
  padding: 2rem;
}
</style>
