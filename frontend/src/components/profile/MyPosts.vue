<template>
  <div class="my-posts">
    <h3 class="section-title">我的帖子</h3>
    
    <div v-if="posts.length === 0 && !loading" class="empty-state">
      <el-empty description="还没有发表过帖子" />
    </div>
    
    <div v-else class="post-list">
      <div 
        v-for="post in posts" 
        :key="post.id" 
        class="post-item"
      >
        <div class="post-header" @click="goToPost(post.id)">
          <h4 class="post-title">{{ post.title }}</h4>
          <div class="post-meta">
            <span>发布于 {{ formatTime(post.createTime) }}</span>
            <span>浏览量 {{ post.viewCount || 0 }}</span>
          </div>
        </div>
        <div class="post-actions">
          <el-button type="primary" size="small" @click="goToEdit(post.id)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(post.id)">删除</el-button>
        </div>
      </div>
    </div>
    
    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        @current-change="fetchPosts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getRecentPosts, deletePost } from '../../api/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { currentUser } from '../../composables/useAuth.js'

const router = useRouter()
const posts = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)

const fetchPosts = async () => {
  if (!currentUser.value) return
  
  loading.value = true
  try {
    const { data } = await getRecentPosts(currentPage.value, pageSize)
    const records = data.data?.records || []
    posts.value = records.filter(p => p.userId === currentUser.value.id)
    total.value = data.data?.total || 0
  } catch (error) {
    console.error('获取帖子失败:', error)
  } finally {
    loading.value = false
  }
}

const goToPost = (id) => {
  router.push(`/posts/${id}`)
}

const goToEdit = (id) => {
  router.push(`/posts/${id}/edit`)
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇帖子吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deletePost(id)
    ElMessage.success('删除成功')
    fetchPosts()
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
  fetchPosts()
})
</script>

<style scoped>
.my-posts {
  padding: 1rem;
}

.section-title {
  margin: 0 0 1.5rem 0;
  font-size: 1.2rem;
  color: #333;
  font-weight: 600;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #409eff;
}

.empty-state {
  padding: 2rem 0;
}

.post-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.post-item {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: box-shadow 0.2s;
}

.post-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.post-header {
  cursor: pointer;
  margin-bottom: 0.75rem;
}

.post-title {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #333;
  font-weight: 600;
}

.post-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #909399;
}

.post-actions {
  display: flex;
  gap: 0.5rem;
  padding-top: 0.75rem;
  border-top: 1px solid #f0f0f0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}
</style>
