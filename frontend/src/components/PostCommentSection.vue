<template>
  <div class="post-comment-section">
    <div class="section-header">
      <el-icon><ChatDotRound /></el-icon>
      <span class="section-title">评论 ({{ total }})</span>
    </div>

    <!-- 发表评论 -->
    <div class="comment-form">
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="3"
        placeholder="请输入评论内容"
        :disabled="!currentUser"
      />
      <div class="form-actions">
        <el-button
          type="primary"
          @click="submitComment"
          :disabled="!currentUser || !newComment.trim()"
        >
          {{ currentUser ? '发表评论' : '请先登录' }}
        </el-button>
        <el-button v-if="isReplying" @click="cancelReply">取消回复</el-button>
      </div>
      <div v-if="isReplying && replyToUser" class="reply-hint">
        回复 @{{ replyToUser }}
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list">
      <el-empty v-if="comments.length === 0" description="暂无评论，快来发表第一条评论吧！" />

      <div v-else>
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-card">
            <div class="comment-header">
              <el-avatar :size="36" :src="comment.userAvatar || null" :icon="UserFilled" class="avatar" />
              <div class="user-info">
                <span class="username">{{ comment.username || '匿名用户' }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
            </div>

            <div class="comment-content">{{ comment.content }}</div>

            <div class="comment-footer">
              <el-button
                v-if="currentUser"
                type="primary"
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
                <div v-else-if="repliesMap[comment.id] && repliesMap[comment.id].data && repliesMap[comment.id].data.length > 0">
                  <div v-for="reply in repliesMap[comment.id].data" :key="reply.id" class="reply-item">
                    <div class="reply-header">
                      <el-avatar :size="28" :src="reply.userAvatar || null" :icon="UserFilled" class="reply-avatar" />
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

                  <!-- 加载更多回复 -->
                  <div v-if="repliesMap[comment.id].hasMore" class="load-more-replies">
                    <el-button
                      type="primary"
                      size="small"
                      :loading="loadingMoreReplies[comment.id]"
                      @click="loadMoreReplies(comment)"
                    >
                      加载更多回复
                    </el-button>
                  </div>
                </div>
                <el-empty v-else description="暂无回复" :image-size="60" />
              </div>
            </el-collapse-transition>
          </div>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, ChatDotRound, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import {
  getPostRootComments,
  getPostRepliesByCursor,
  createPostComment,
  deletePostComment as apiDeletePostComment
} from '../api/index.js'
import { formatTime } from '../utils/format.js'

const props = defineProps({
  postId: {
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
const loadingMoreReplies = reactive({})
const currentPage = ref(1)
const totalPages = ref(1)
const total = ref(0)

const isReplying = ref(false)
const replyToComment = ref(null)
const replyToUser = ref(null)
const replyToUserId = ref(null)

// 获取评论列表
const fetchComments = async () => {
  try {
    const { data } = await getPostRootComments(props.postId, currentPage.value, 10)
    comments.value = data.data.records
    total.value = data.data.total
    totalPages.value = data.data.pages
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

// 提交评论
const submitComment = async () => {
  if (!newComment.value.trim()) return

  try {
    const requestData = {
      content: newComment.value.trim(),
      postId: props.postId
    }

    if (isReplying.value && replyToComment.value) {
      requestData.rootId = replyToComment.value.id
      requestData.parentId = replyToComment.value.id
      if (replyToUserId.value) {
        requestData.replyToUserId = replyToUserId.value
      }
      // 如果回复的是回复，则 rootId 保持根评论 ID
      if (replyToComment.value.rootId && replyToComment.value.rootId !== replyToComment.value.id) {
        requestData.rootId = replyToComment.value.rootId
        requestData.parentId = replyToComment.value.id
      }
    }

    await createPostComment(requestData)
    ElMessage.success('评论成功')
    newComment.value = ''
    cancelReply()
    fetchComments()
    emit('comment-added')
  } catch (error) {
    console.error('评论失败:', error)
  }
}

// 开始回复
const startReply = (comment, reply = null) => {
  isReplying.value = true
  replyToComment.value = comment
  if (reply) {
    replyToUser.value = reply.username
    replyToUserId.value = reply.userId
  } else {
    replyToUser.value = comment.username
    replyToUserId.value = comment.userId
  }
  // 滚动到评论框
  document.querySelector('.comment-form')?.scrollIntoView({ behavior: 'smooth' })
}

// 取消回复
const cancelReply = () => {
  isReplying.value = false
  replyToComment.value = null
  replyToUser.value = null
  replyToUserId.value = null
}

// 删除评论
const deleteCommentHandler = async (commentId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await apiDeletePostComment(commentId)
    ElMessage.success('删除成功')
    fetchComments()
    emit('comment-deleted')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
    }
  }
}

// 展开/收起回复
const toggleReplies = async (comment) => {
  if (expandedComments[comment.id]) {
    expandedComments[comment.id] = false
    return
  }

  expandedComments[comment.id] = true
  loadingReplies[comment.id] = true

  try {
    const { data } = await getPostRepliesByCursor(comment.id, null, 20)
    repliesMap[comment.id] = data.data
  } catch (error) {
    console.error('获取回复失败:', error)
  } finally {
    loadingReplies[comment.id] = false
  }
}

// 加载更多回复
const loadMoreReplies = async (comment) => {
  loadingMoreReplies[comment.id] = true
  try {
    const cursor = repliesMap[comment.id].nextCursor
    const { data } = await getPostRepliesByCursor(comment.id, cursor, 20)
    const newReplies = data.data
    repliesMap[comment.id] = {
      data: [...repliesMap[comment.id].data, ...newReplies.data],
      nextCursor: newReplies.nextCursor,
      hasMore: newReplies.hasMore
    }
  } catch (error) {
    console.error('加载更多回复失败:', error)
  } finally {
    loadingMoreReplies[comment.id] = false
  }
}

// 分页
const handlePageChange = (page) => {
  currentPage.value = page
  fetchComments()
}

onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.post-comment-section {
  margin-top: 2rem;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
}

.section-header .el-icon {
  color: #409eff;
}

.comment-form {
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: #fafafa;
  border-radius: 8px;
}

.comment-form .el-textarea {
  margin-bottom: 1rem;
}

.form-actions {
  display: flex;
  gap: 0.5rem;
}

.reply-hint {
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #409eff;
}

.comment-list {
  margin-top: 1rem;
}

.comment-item {
  margin-bottom: 1rem;
}

.comment-card {
  padding: 1rem;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  transition: box-shadow 0.3s;
}

.comment-card:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
}

.avatar {
  flex-shrink: 0;
  background-color: #e8e8e8;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.username {
  font-weight: 500;
  color: #409eff;
  font-size: 0.9rem;
}

.comment-time {
  color: #999;
  font-size: 0.8rem;
}

.comment-content {
  font-size: 0.95rem;
  line-height: 1.6;
  color: #333;
  margin-bottom: 0.75rem;
  white-space: pre-wrap;
}

.comment-footer {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.reply-count-tag {
  cursor: pointer;
}

.arrow-icon {
  margin-left: 4px;
}

/* 回复列表 */
.replies-container {
  margin-top: 1rem;
  padding-top: 1rem;
  padding-left: 2rem;
  border-top: 1px solid #f0f0f0;
}

.reply-item {
  padding: 0.75rem 0;
  border-bottom: 1px solid #f5f5f5;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.reply-avatar {
  flex-shrink: 0;
  background-color: #e8e8e8;
}

.reply-user-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
}

.reply-time {
  color: #999;
  font-size: 0.75rem;
}

.reply-to {
  color: #409eff;
  font-size: 0.8rem;
}

.reply-content {
  font-size: 0.9rem;
  line-height: 1.5;
  color: #555;
  margin-bottom: 0.5rem;
  padding-left: 2.5rem;
  white-space: pre-wrap;
}

.reply-footer {
  padding-left: 2.5rem;
}

.load-more-replies {
  text-align: center;
  padding: 0.75rem 0;
}

.pagination {
  margin-top: 1.5rem;
  display: flex;
  justify-content: center;
}
</style>
