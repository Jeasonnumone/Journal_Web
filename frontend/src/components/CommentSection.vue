<template>
  <div class="comment-section">
    <div class="section-header">
      <el-icon><ChatDotRound /></el-icon>
      <span class="section-title">评论区域</span>
    </div>
    
    <!-- 发表评论表单 -->
    <div class="comment-form">
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="4"
        placeholder="写下你的评论..."
        class="comment-input"
      ></el-input>
      <div class="form-actions">
        <el-button 
          type="primary" 
          @click="submitComment" 
          :disabled="!newComment.trim() || !currentUser"
        >
          {{ currentUser ? '发表评论' : '请先登录' }}
        </el-button>
        <el-button v-if="isReplying" @click="cancelReply">取消回复</el-button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <el-empty v-if="comments.length === 0" description="暂无评论，快来发表第一条评论吧！" />
      
      <div v-else>
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <el-card shadow="hover" class="comment-card">

            <div class="comment-header">
              <el-avatar :size="36" :icon="UserFilled" class="avatar" />
              <div class="user-info">
                <span class="username">{{ comment.username || '匿名用户' }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
            </div>
            
            <div class="comment-content">{{ comment.content }}</div>
            
            <div class="comment-footer">
              <el-button 
                v-if="currentUser" 
                type="info" 
                size="small" 
                text 
                @click="startReply(comment)"
              >回复</el-button>
              <el-button 
                v-if="currentUser && currentUser.id === comment.userId" 
                type="danger" 
                size="small" 
                text 
                @click="deleteCommentHandler(comment.id)"
              >删除</el-button>
              <el-tag 
                v-if="comment.replyCount > 0" 
                size="medium" 
                type="info" 
                effect="light"
                class="reply-count-tag"
                @click="toggleReplies(comment)"
              >
                {{ comment.replyCount }} 条回复
                <el-icon class="arrow-icon">
                  <!-- <component :is="expandedComments[comment.id] ? 'ArrowUp' : 'ArrowDown'" /> -->
                  <ArrowUp v-if="expandedComments[comment.id]" />
                  <ArrowDown v-else />
                </el-icon>
              </el-tag>
            </div>

            <!-- 回复列表 -->
            <el-collapse-transition>
              <div v-if="expandedComments[comment.id]" class="replies-container">
                <div v-if="loadingReplies[comment.id]" class="loading">
                  <el-skeleton :rows="2" animated />
                </div>
                <div v-else-if="repliesMap[comment.id] && repliesMap[comment.id].length > 0">
                  <div v-for="reply in repliesMap[comment.id]" :key="reply.id" class="reply-item">

                    <div class="reply-header">
                      <el-avatar :size="28" :icon="UserFilled" class="reply-avatar" />
                      <div class="reply-user-info">
                        <span class="username">{{ reply.username || '匿名用户' }}</span>
                        <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                        <span v-if="reply.replyToUsername" class="reply-to">
                          回复 @{{ reply.replyToUsername }}
                        </span>
                      </div>
                    </div>

                    <div class="reply-content">{{ reply.content }}</div>

                    <div class="reply-footer">
                      <el-button 
                        v-if="currentUser" 
                        type="primary" 
                        size="small" 
                        text 
                        @click="startReply(comment, reply)"
                      >回复</el-button>
                      <el-button 
                        v-if="currentUser && currentUser.id === reply.userId" 
                        type="danger" 
                        size="small" 
                        text 
                        @click="deleteCommentHandler(reply.id)"
                      >删除</el-button>
                    </div>
                  </div>

                </div>
                <el-empty v-else description="暂无回复" :image-size="60" />
              </div>
            </el-collapse-transition>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        layout="prev, pager, next"
        :total="total"
        :page-size="10"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, ChatDotRound, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { getRootComments, getReplies, createComment, deleteComment as apiDeleteComment } from '../api/index.js'

const props = defineProps({
  journalId: {
    type: Number,
    required: true
  },
  currentUser: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['comment-added', 'comment-deleted'])

const newComment = ref('')
const comments = ref([])
const repliesMap = reactive({})
const expandedComments = reactive({})
const loadingReplies = reactive({})
const currentPage = ref(1)
const totalPages = ref(1)
const total = ref(0)

const isReplying = ref(false)
const replyToComment = ref(null)
const replyToUser = ref(null)

const loadComments = async () => {
  try {
    const response = await getRootComments(props.journalId, currentPage.value, 10)
    const pageData = response.data.data
    comments.value = pageData.records || []
    total.value = pageData.total || 0
    totalPages.value = Math.ceil(total.value / (pageData.size || 10))
  } catch (error) {
    console.error('加载评论失败:', error)
    ElMessage.error('加载评论失败')
  }
}

const loadReplies = async (comment) => {
  if (loadingReplies[comment.id]) return
  
  loadingReplies[comment.id] = true
  try {
    const response = await getReplies(comment.rootId || comment.id, 1, 20)
    const pageData = response.data.data
    repliesMap[comment.id] = (pageData.records || []).filter(r => r.id !== comment.id)
  } catch (error) {
    console.error('加载回复失败:', error)
    repliesMap[comment.id] = []
  } finally {
    loadingReplies[comment.id] = false
  }
}

const toggleReplies = async (comment) => {
  if (expandedComments[comment.id]) {
    expandedComments[comment.id] = false
  } else {
    expandedComments[comment.id] = true
    if (!repliesMap[comment.id]) {
      await loadReplies(comment)
    }
  }
}

const submitComment = async () => {
  if (!newComment.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  if (!props.currentUser) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    const commentData = {
      journalId: props.journalId,
      content: newComment.value.trim()
    }

    if (isReplying.value && replyToComment.value) {
      commentData.rootId = replyToComment.value.rootId || replyToComment.value.id
      commentData.parentId = replyToComment.value.id
      if (replyToUser.value) {
        commentData.replyToUserId = replyToUser.value.userId
      }
    }

    await createComment(commentData)
    
    // 保存根评论 ID，用于刷新回复
    const rootId = replyToComment.value?.rootId || replyToComment.value?.id
    const wasExpanded = isReplying.value && expandedComments[rootId]
    
    // 重置状态
    newComment.value = ''
    isReplying.value = false
    replyToComment.value = null
    replyToUser.value = null
    
    ElMessage.success('评论成功')
    await loadComments()
    
    // 如果是回复且列表展开中，重新加载回复
    if (wasExpanded && rootId) {
      const rootComment = comments.value.find(c => c.id === rootId)
      if (rootComment) await loadReplies(rootComment)
    }
    
    emit('comment-added')
  } catch (error) {
    console.error('发表评论失败:', error)
    ElMessage.error('评论失败：' + (error.response?.data?.message || error.message))
  }
}

const startReply = (comment, reply = null) => {
  isReplying.value = true
  replyToComment.value = comment
  replyToUser.value = reply
  newComment.value = ''
}

const cancelReply = () => {
  isReplying.value = false
  replyToComment.value = null
  replyToUser.value = null
  newComment.value = ''
}

const deleteCommentHandler = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    
    await apiDeleteComment(commentId)
    await loadComments()
    
    for (const key in repliesMap) {
      if (repliesMap[key]) {
        repliesMap[key] = repliesMap[key].filter(r => r.id !== commentId)
      }
    }
    
    ElMessage.success('删除成功')
    emit('comment-deleted')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
      ElMessage.error('删除失败：' + (error.response?.data?.message || error.message))
    }
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  loadComments()
}

const formatTime = (timeString) => {
  if (!timeString) return ''
  const date = new Date(timeString)
  const now = new Date()
  const diff = now - date
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  
  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`
  } else if (diff < 7 * day) {
    return `${Math.floor(diff / day)}天前`
  } else {
    return date.toLocaleDateString('zh-CN')
  }
}

watch(() => props.journalId, () => {
  currentPage.value = 1
  loadComments()
})

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-section {
  margin-top: 2rem;
  padding: 2rem;
  background: white;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.section-header .el-icon {
  font-size: 1.5rem;
  color: #667eea;
}

.section-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: #333;
}

.comment-form {
  margin-bottom: 2rem;
}

.comment-input {
  margin-bottom: 1rem;
}

.form-actions {
  display: flex;
  gap: 0.5rem;
}

.comment-list {
  margin-top: 1.5rem;
}

.comment-item {
  margin-bottom: 1rem;
}

.comment-card {
  border-radius: 8px;
  transition: all 0.3s;
}

.comment-card:hover {
  transform: translateY(-2px);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.username {
  font-weight: bold;
  color: #667eea;
  font-size: 0.95rem;
}

.comment-time {
  font-size: 0.8rem;
  color: #999;
}

.comment-content {
  color: #333;
  line-height: 1.6;
  margin-bottom: 1rem;
  font-size: 1rem;
}

.comment-footer {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding-top: 0.75rem;
  border-top: 1px solid #f0f0f0;
}

.reply-count-tag {
  cursor: pointer;
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.reply-count-tag:hover {
  opacity: 0.8;
}

.arrow-icon {
  font-size: 0.8rem;
}

.replies-container {
  margin-top: 1rem;
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 8px;
}

.loading {
  padding: 1rem;
}

.reply-item {
  padding: 1rem 0;
  border-bottom: 1px solid #eee;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.reply-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.reply-user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.reply-user-info .username {
  font-size: 0.9rem;
}

.reply-time {
  font-size: 0.75rem;
  color: #999;
}

.reply-to {
  color: #999;
  font-size: 0.8rem;
}

.reply-content {
  color: #333;
  line-height: 1.5;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
}

.reply-footer {
  display: flex;
  gap: 0.5rem;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}
</style>
