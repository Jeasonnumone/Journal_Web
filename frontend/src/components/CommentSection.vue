<template>
  <div class="comment-section">
    <h3 class="section-title">评论区域</h3>
    
    <!-- 发表评论表单 -->
    <div class="comment-form">
      <textarea 
        v-model="newComment" 
        placeholder="写下你的评论..." 
        class="comment-input"
        rows="3"
      ></textarea>
      <div class="form-actions">
        <button 
          @click="submitComment" 
          :disabled="!newComment.trim() || !currentUser"
          class="submit-btn"
        >
          {{ currentUser ? '发表评论' : '请先登录' }}
        </button>
        <button v-if="isReplying" @click="cancelReply" class="cancel-btn">取消回复</button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <div v-if="comments.length === 0" class="no-comments">
        暂无评论，快来发表第一条评论吧！
      </div>
      <div v-else>
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <span class="username">{{ comment.username || '匿名用户' }}</span>
            <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
          </div>
          <div class="comment-content">{{ comment.content }}</div>
          <div class="comment-footer">
            <button 
              v-if="currentUser" 
              @click="startReply(comment)" 
              class="reply-btn"
            >
              回复
            </button>
            <button 
              v-if="currentUser && currentUser.id === comment.userId" 
              @click="deleteCommentHandler(comment.id)" 
              class="delete-btn"
            >
              删除
            </button>
            <span 
              v-if="comment.replyCount > 0" 
              @click="toggleReplies(comment)" 
              class="reply-count"
            >
              {{ comment.replyCount }} 条回复
              <span class="arrow">{{ expandedComments[comment.id] ? '▲' : '▼' }}</span>
            </span>
          </div>

          <!-- 回复列表 -->
          <div v-if="expandedComments[comment.id]" class="replies-container">
            <div v-if="loadingReplies[comment.id]" class="loading">加载中...</div>
            <div v-else-if="repliesMap[comment.id] && repliesMap[comment.id].length > 0">
              <div v-for="reply in repliesMap[comment.id]" :key="reply.id" class="reply-item">
                <div class="reply-header">
                  <span class="username">{{ reply.username || '匿名用户' }}</span>
                  <span class="reply-time">{{ formatTime(reply.createTime) }}</span>
                  <span v-if="reply.replyToUsername" class="reply-to">
                    回复 @{{ reply.replyToUsername }}
                  </span>
                </div>
                <div class="reply-content">{{ reply.content }}</div>
                <div class="reply-footer">
                  <button v-if="currentUser" @click="startReply(comment, reply)" class="reply-btn">
                    回复
                  </button>
                  <button 
                    v-if="currentUser && currentUser.id === reply.userId" 
                    @click="deleteCommentHandler(reply.id)" 
                    class="delete-btn"
                  >
                    删除
                  </button>
                </div>
              </div>
            </div>
            <div v-else class="no-replies">暂无回复</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="pagination">
      <button @click="prevPage" :disabled="currentPage === 1" class="page-btn">上一页</button>
      <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页</span>
      <button @click="nextPage" :disabled="currentPage === totalPages" class="page-btn">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
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
  if (!newComment.value.trim()) return
  if (!props.currentUser) {
    alert('请先登录')
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
    newComment.value = ''
    isReplying.value = false
    replyToComment.value = null
    replyToUser.value = null
    
    await loadComments()
    emit('comment-added')
  } catch (error) {
    console.error('发表评论失败:', error)
    alert('评论失败：' + (error.response?.data?.message || error.message))
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
  if (!confirm('确定要删除这条评论吗？')) return

  try {
    await apiDeleteComment(commentId)
    await loadComments()
    
    for (const key in repliesMap) {
      if (repliesMap[key]) {
        repliesMap[key] = repliesMap[key].filter(r => r.id !== commentId)
      }
    }
    
    emit('comment-deleted')
  } catch (error) {
    console.error('删除评论失败:', error)
    alert('删除失败：' + (error.response?.data?.message || error.message))
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadComments()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadComments()
  }
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
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.section-title {
  margin: 0 0 1.5rem 0;
  color: #333;
  font-size: 1.5rem;
}

.comment-form {
  margin-bottom: 2rem;
}

.comment-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 1rem;
  font-family: inherit;
  resize: vertical;
  box-sizing: border-box;
}

.comment-input:focus {
  outline: none;
  border-color: #667eea;
}

.form-actions {
  display: flex;
  gap: 0.5rem;
  margin-top: 0.75rem;
}

.submit-btn {
  padding: 0.5rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.submit-btn:hover:not(:disabled) {
  background: #5568d3;
}

.cancel-btn {
  padding: 0.5rem 1.5rem;
  background: #f0f0f0;
  color: #666;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-size: 1rem;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

.comment-list {
  margin-top: 1.5rem;
}

.no-comments {
  text-align: center;
  color: #999;
  padding: 2rem;
}

.comment-item {
  padding: 1rem 0;
  border-bottom: 1px solid #eee;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}

.username {
  font-weight: bold;
  color: #667eea;
}

.comment-time {
  font-size: 0.85rem;
  color: #999;
}

.comment-content {
  color: #333;
  line-height: 1.6;
  margin-bottom: 0.75rem;
}

.comment-footer {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.reply-btn,
.delete-btn {
  padding: 0.25rem 0.75rem;
  background: transparent;
  border: 1px solid #667eea;
  color: #667eea;
  border-radius: 3px;
  cursor: pointer;
  font-size: 0.85rem;
}

.reply-btn:hover,
.delete-btn:hover {
  background: #667eea;
  color: white;
}

.delete-btn {
  border-color: #dc3545;
  color: #dc3545;
}

.delete-btn:hover {
  background: #dc3545;
  color: white;
}

.reply-count {
  color: #667eea;
  cursor: pointer;
  font-size: 0.85rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.reply-count:hover {
  text-decoration: underline;
}

.arrow {
  font-size: 0.75rem;
}

.replies-container {
  margin-top: 1rem;
  padding: 1rem;
  background: #f9f9f9;
  border-radius: 5px;
}

.loading,
.no-replies {
  text-align: center;
  color: #999;
  padding: 1rem;
}

.reply-item {
  padding: 0.75rem 0;
  border-bottom: 1px solid #eee;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}

.reply-to {
  color: #999;
  font-size: 0.85rem;
}

.reply-content {
  color: #333;
  line-height: 1.5;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
}

.reply-footer {
  display: flex;
  gap: 1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #eee;
}

.page-btn {
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.page-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.page-info {
  color: #666;
}
</style>
