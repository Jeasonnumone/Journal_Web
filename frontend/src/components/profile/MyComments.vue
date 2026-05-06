<template>
  <div class="my-comments">
    <h3 class="section-title">我的评论</h3>
    
    <div v-if="comments.length === 0 && !loading" class="empty-state">
      <el-empty description="还没有发表过评论" />
    </div>
    
    <div v-else class="comment-list">
      <div 
        v-for="comment in comments" 
        :key="comment.id" 
        class="comment-item"
      >
        <div class="comment-header" @click="goToJournal(comment.journalId)">
          <span class="journal-title">评论了：{{ comment.journalTitle || '未知期刊' }}</span>
          <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
        <div class="comment-actions">
          <el-button type="danger" size="small" @click="handleDelete(comment.id)">删除</el-button>
        </div>
      </div>
    </div>
    
    <div class="pagination" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        @current-change="fetchComments"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserComments, deleteComment } from '../../api/index.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const comments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)

const fetchComments = async () => {
  loading.value = true
  try {
    const { data } = await getUserComments(currentPage.value, pageSize)
    comments.value = data.data?.records || []
    total.value = data.data?.total || 0
  } catch (error) {
    console.error('获取评论失败:', error)
  } finally {
    loading.value = false
  }
}

const goToJournal = (journalId) => {
  if (journalId) {
    router.push(`/journal/${journalId}`)
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await deleteComment(id)
    ElMessage.success('删除成功')
    fetchComments()
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
  fetchComments()
})
</script>

<style scoped>
.my-comments {
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

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-item {
  padding: 1rem;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: box-shadow 0.2s;
}

.comment-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  cursor: pointer;
}

.journal-title {
  font-weight: 500;
  color: #409eff;
}

.comment-time {
  font-size: 0.85rem;
  color: #909399;
}

.comment-content {
  color: #606266;
  font-size: 0.9rem;
  line-height: 1.5;
  margin-bottom: 0.75rem;
}

.comment-actions {
  display: flex;
  justify-content: flex-end;
  padding-top: 0.75rem;
  border-top: 1px solid #f0f0f0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}
</style>
