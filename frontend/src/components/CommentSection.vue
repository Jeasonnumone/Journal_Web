<template>
  <div class="comment-section">
    <div class="section-header">
      <el-icon><ChatDotRound /></el-icon>
      <span class="section-title">评论区域</span>
    </div>
    
    <!-- 发表评论表单 -->
    <div class="comment-form">
      <!-- 投稿体验详情 -->
      <div class="experience-form">
        <el-form label-width="100px" size="default">
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="审稿时间">
                <el-select v-model="experience.reviewTime" placeholder="--请选择--">
                  <el-option label="1周内" value="1周内" />
                  <el-option label="1个月以内" value="1个月以内" />
                  <el-option label="1-3月" value="1-3月" />
                  <el-option label="3-6月" value="3-6月" />
                  <el-option label="6-12月" value="6-12月" />
                  <el-option label="1年以上" value="1年以上" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否录用">
                <el-select v-model="experience.isAccepted" placeholder="--请选择--">
                  <el-option label="已录用" value="已录用" />
                  <el-option label="已拒稿" value="已拒稿" />
                  <el-option label="审稿中" value="审稿中" />
                  <el-option label="待审稿" value="待审稿" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="发表排期">
                <el-select v-model="experience.publishPeriod" placeholder="--请选择--">
                  <el-option label="1个月" value="1个月" />
                  <el-option label="1-3月" value="1-3月" />
                  <el-option label="3-6月" value="3-6月" />
                  <el-option label="6-12月" value="6-12月" />
                  <el-option label="1年以上" value="1年以上" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否首发">
                <el-select v-model="experience.isFirstPublish" placeholder="--请选择--">
                  <el-option label="是" value="是" />
                  <el-option label="否" value="否" />
                  <el-option label="不确定" value="不确定" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="审稿费用">
                <el-input v-model="experience.reviewFeeStr" placeholder="0">
                  <template #append>元</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="版面费">
                <el-input v-model="experience.pageFeeStr" placeholder="0">
                  <template #append>元</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="稿费">
                <el-input v-model="experience.paymentStr" placeholder="0">
                  <template #append>元</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="稿件字数">
                <el-input v-model="experience.wordCountStr" placeholder="0">
                  <template #append>字</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="我的学历">
                <el-select v-model="experience.education" placeholder="--请选择--">
                  <el-option label="专科以下" value="专科以下" />
                  <el-option label="专科" value="专科" />
                  <el-option label="本科" value="本科" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="博士" value="博士" />
                  <el-option label="博士后" value="博士后" />
                  <el-option label="其他" value="其他" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="我的职称">
                <el-select v-model="experience.title" placeholder="--请选择--">
                  <el-option label="无职称" value="无职称" />
                  <el-option label="助教" value="助教" />
                  <el-option label="讲师" value="讲师" />
                  <el-option label="副教授" value="副教授" />
                  <el-option label="教授" value="教授" />
                  <el-option label="助理研究员" value="助理研究员" />
                  <el-option label="副研究员" value="副研究员" />
                  <el-option label="研究员" value="研究员" />
                  <el-option label="助理工程师" value="助理工程师" />
                  <el-option label="工程师" value="工程师" />
                  <el-option label="高级工程师" value="高级工程师" />
                  <el-option label="正高级工程师" value="正高级工程师" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="有无课题">
                <el-select v-model="experience.hasProject" placeholder="--请选择--">
                  <el-option label="无课题" value="无课题" />
                  <el-option label="研究生专创课题" value="研究生专创课题" />
                  <el-option label="院级课题" value="院级课题" />
                  <el-option label="校级课题" value="校级课题" />
                  <el-option label="市厅级课题" value="市厅级课题" />
                  <el-option label="省部级课题" value="省部级课题" />
                  <el-option label="国家级课题" value="国家级课题" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="有无回复">
                <el-select v-model="experience.hasReply" placeholder="--请选择--">
                  <el-option label="有详细回复" value="有详细回复" />
                  <el-option label="有简单回复" value="有简单回复" />
                  <el-option label="无回复" value="无回复" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="该刊可发">
                <el-select v-model="experience.publishType" placeholder="--请选择--">
                  <el-option label="学术论文" value="学术论文" />
                  <el-option label="综述文章" value="综述文章" />
                  <el-option label="教学论文" value="教学论文" />
                  <el-option label="实践报告" value="实践报告" />
                  <el-option label="其他类型" value="其他类型" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="投稿主题">
                <el-input v-model="experience.topic" placeholder="简述投稿主题或研究方向" />
              </el-form-item>
            </el-col>
          </el-row>

          <!-- 点评配图 单独一行 -->
          <el-divider content-position="left">点评配图（最多上传三张）</el-divider>
          <el-row :gutter="24">
            <el-col :span="24">
              <div class="image-upload-wrapper">
                <el-upload
                  action="#"
                  list-type="picture-card"
                  :auto-upload="false"
                  :limit="5"
                  :on-change="handleImageChange"
                  :on-remove="handleImageRemove"
                  accept="image/*"
                >
                  <el-icon><Plus /></el-icon>
                </el-upload>
              </div>
            </el-col>
          </el-row>

          <!-- 我的点评 单独一行 -->
          <el-divider content-position="left"></el-divider>
          <el-row :gutter="24">
            <el-col :span="24">
              <el-input
                v-model="newComment"
                type="textarea"
                :rows="5"
                placeholder="请分享您的评论吧"
              ></el-input>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <div class="form-actions">
        <el-button 
          type="primary" 
          @click="submitComment" 
          :disabled="!currentUser"
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
              <el-avatar :size="36" :src="comment.userAvatar || null" :icon="UserFilled" class="avatar" />
              <div class="user-info">
                <span class="username">{{ comment.username || '匿名用户' }}</span>
                <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
              </div>
            </div>
            
            <div class="comment-content">{{ comment.content }}</div>
            
            <!-- 投稿体验信息展示 -->
            <div v-if="hasExperienceData(comment)" class="experience-info">
              <div class="experience-tags">
                <el-tag v-if="comment.reviewTime" size="small" type="warning">审稿时间: {{ comment.reviewTime }}</el-tag>
                <el-tag v-if="comment.isAccepted" size="small" :type="getAcceptedTagType(comment.isAccepted)">是否录用: {{ comment.isAccepted }}</el-tag>
                <el-tag v-if="comment.publishPeriod" size="small" type="warning">发表排期: {{ comment.publishPeriod }}</el-tag>
                <el-tag v-if="comment.isFirstPublish" size="small" type="warning">是否首发: {{ comment.isFirstPublish }}</el-tag>
                <el-tag v-if="comment.reviewFee != null" size="small" type="warning">审稿费用: {{ comment.reviewFee }}元</el-tag>
                <el-tag v-if="comment.pageFee != null" size="small" type="warning">版面费: {{ comment.pageFee }}元</el-tag>
                <el-tag v-if="comment.payment != null" size="small" type="warning">稿费: {{ comment.payment }}元</el-tag>
                <el-tag v-if="comment.wordCount != null" size="small" type="warning">稿件字数: {{ comment.wordCount }}字</el-tag>
                <el-tag v-if="comment.education" size="small" type="warning">学历: {{ comment.education }}</el-tag>
                <el-tag v-if="comment.title" size="small" type="warning">职称: {{ comment.title }}</el-tag>
                <el-tag v-if="comment.hasProject" size="small" type="warning">课题: {{ comment.hasProject }}</el-tag>
                <el-tag v-if="comment.hasReply" size="small" type="warning">回复: {{ comment.hasReply }}</el-tag>
                <el-tag v-if="comment.publishType" size="small" type="warning">可发: {{ comment.publishType }}</el-tag>
                <el-tag v-if="comment.topic" size="small" type="warning">主题: {{ comment.topic }}</el-tag>
              </div>
            </div>
            
            <!-- 评论图片展示 -->
            <div v-if="comment.images && parseImages(comment.images).length > 0" class="comment-images">
              <el-image 
                v-for="(imgUrl, index) in parseImages(comment.images)" 
                :key="index"
                :src="imgUrl"
                :preview-src-list="parseImages(comment.images)"
                :initial-index="index"
                fit="cover"
                class="comment-image"
              />
            </div>
            
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

                  <!-- 加载更多按钮 -->
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
import { UserFilled, ChatDotRound, ArrowUp, ArrowDown, Plus } from '@element-plus/icons-vue'
import { getRootComments, getRepliesByCursor, createComment, uploadCommentImage, deleteComment as apiDeleteComment } from '../api/index.js'
import { formatTime } from '../utils/format.js'

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
// 回复列表改为游标分页结构：{ data: [], cursor: null, hasMore: false }
const repliesMap = reactive({})
const expandedComments = reactive({})
const loadingReplies = reactive({})
// 加载更多回复的状态
const loadingMoreReplies = reactive({})
const currentPage = ref(1)
const totalPages = ref(1)
const total = ref(0)

const isReplying = ref(false)
const replyToComment = ref(null)
const replyToUser = ref(null)

// 投稿体验表单数据
const experience = reactive({
  reviewTime: '',
  isAccepted: '',
  publishPeriod: '',
  isFirstPublish: '',
  reviewFeeStr: '',
  pageFeeStr: '',
  paymentStr: '',
  wordCountStr: '',
  education: '',
  title: '',
  hasProject: '',
  hasReply: '',
  publishType: '',
  topic: ''
})

// 点评配图 - 存储已上传的图片URL
const uploadedImageUrls = ref([])

const handleImageChange = async (file) => {
  if (!props.currentUser) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const response = await uploadCommentImage(file.raw)
    const imageUrl = response.data.data
    // 将URL和file的uid关联，方便删除时对应
    uploadedImageUrls.value.push({ uid: file.uid, url: imageUrl })
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
  }
}

const handleImageRemove = (file) => {
  const index = uploadedImageUrls.value.findIndex(img => img.uid === file.uid)
  if (index > -1) {
    uploadedImageUrls.value.splice(index, 1)
  }
}

// 重置表单
const resetForm = () => {
  newComment.value = ''
  experience.reviewTime = ''
  experience.isAccepted = ''
  experience.publishPeriod = ''
  experience.isFirstPublish = ''
  experience.reviewFeeStr = ''
  experience.pageFeeStr = ''
  experience.paymentStr = ''
  experience.wordCountStr = ''
  experience.education = ''
  experience.title = ''
  experience.hasProject = ''
  experience.hasReply = ''
  experience.publishType = ''
  experience.topic = ''
  uploadedImageUrls.value = []
}

// 检查评论是否有投稿体验数据
const hasExperienceData = (comment) => {
  return comment.reviewTime || 
         comment.isAccepted || 
         comment.publishPeriod || 
         comment.isFirstPublish || 
         comment.reviewFee != null || 
         comment.pageFee != null || 
         comment.payment != null || 
         comment.wordCount != null || 
         comment.education || 
         comment.title || 
         comment.hasProject || 
         comment.hasReply || 
         comment.publishType || 
         comment.topic
}

// 根据录用状态返回标签类型
const getAcceptedTagType = (isAccepted) => {
  if (isAccepted === '已录用') return 'success'
  if (isAccepted === '已拒稿') return 'danger'
  return 'warning'
}

// 解析图片URL列表
const parseImages = (imagesStr) => {
  if (!imagesStr) return []
  try {
    return JSON.parse(imagesStr)
  } catch (e) {
    return []
  }
}

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
    // 使用游标分页加载回复（首次加载，cursor 为 null）
    const response = await getRepliesByCursor(comment.rootId || comment.id, null, 20)
    const cursorData = response.data.data
    
    // 存储游标分页数据
    repliesMap[comment.id] = {
      data: (cursorData.data || []).filter(r => r.id !== comment.id),
      cursor: cursorData.nextCursor,
      hasMore: cursorData.hasMore || false
    }
  } catch (error) {
    console.error('加载回复失败:', error)
    repliesMap[comment.id] = { data: [], cursor: null, hasMore: false }
  } finally {
    loadingReplies[comment.id] = false
  }
}

// 加载更多回复（游标分页）
const loadMoreReplies = async (comment) => {
  if (loadingMoreReplies[comment.id]) return
  
  const replyData = repliesMap[comment.id]
  if (!replyData || !replyData.cursor) return
  
  loadingMoreReplies[comment.id] = true
  try {
    const response = await getRepliesByCursor(comment.rootId || comment.id, replyData.cursor, 20)
    const cursorData = response.data.data
    
    // 追加新数据
    replyData.data = [...replyData.data, ...(cursorData.data || []).filter(r => r.id !== comment.id)]
    replyData.cursor = cursorData.nextCursor
    replyData.hasMore = cursorData.hasMore || false
  } catch (error) {
    console.error('加载更多回复失败:', error)
    ElMessage.error('加载更多回复失败')
  } finally {
    loadingMoreReplies[comment.id] = false
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
  if (!props.currentUser) {
    ElMessage.warning('请先登录')
    return
  }

  try {
    const commentData = {
      journalId: props.journalId,
      content: newComment.value.trim(),
      // 投稿体验详情
      reviewTime: experience.reviewTime,
      isAccepted: experience.isAccepted,
      publishPeriod: experience.publishPeriod,
      isFirstPublish: experience.isFirstPublish,
      reviewFee: experience.reviewFeeStr ? Number(experience.reviewFeeStr) : null,
      pageFee: experience.pageFeeStr ? Number(experience.pageFeeStr) : null,
      payment: experience.paymentStr ? Number(experience.paymentStr) : null,
      wordCount: experience.wordCountStr ? Number(experience.wordCountStr) : null,
      education: experience.education,
      title: experience.title,
      hasProject: experience.hasProject,
      hasReply: experience.hasReply,
      publishType: experience.publishType,
      topic: experience.topic
    }

    if (isReplying.value && replyToComment.value) {
      commentData.rootId = replyToComment.value.rootId || replyToComment.value.id
      commentData.parentId = replyToComment.value.id
      if (replyToUser.value) {
        commentData.replyToUserId = replyToUser.value.userId
      }
    }

    // 如果有已上传的图片，将URL列表作为images字段提交
    if (uploadedImageUrls.value.length > 0) {
      commentData.images = JSON.stringify(uploadedImageUrls.value.map(img => img.url))
    }

    await createComment(commentData)
    
    // 保存根评论 ID，用于刷新回复
    const rootId = replyToComment.value?.rootId || replyToComment.value?.id
    const wasExpanded = isReplying.value && expandedComments[rootId]
    
    // 重置状态
    resetForm()
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
  resetForm()
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
    
    // 从回复列表中删除（适配新的数据结构）
    for (const key in repliesMap) {
      if (repliesMap[key] && repliesMap[key].data) {
        repliesMap[key].data = repliesMap[key].data.filter(r => r.id !== commentId)
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

.experience-form {
  background: #fafbfc;
  padding: 1.5rem;
  border-radius: 10px;
  border: 1px solid #e8ecf0;
  margin-bottom: 1.5rem;
}

.experience-form :deep(.el-form-item) {
  margin-bottom: 18px;
}

.experience-form :deep(.el-form-item__label) {
  color: #606266;
  font-weight: 500;
  font-size: 13px;
}

.experience-form :deep(.el-select),
.experience-form :deep(.el-input) {
  width: 100%;
}

.experience-form :deep(.el-divider__text) {
  font-size: 13px;
  color: #909399;
  font-weight: 500;
}

.image-upload-wrapper {
  display: flex;
  align-items: flex-start;
  gap: 12px;
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

.experience-info {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 12px 16px;
  margin-bottom: 1rem;
  border-left: 3px solid #667eea;
  border-right: 3px solid #667eea;
}

.experience-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.experience-tags .el-tag {
  font-size: 12px;
  width: calc((100% - 40px) / 6);
  text-align: center;
  justify-content: center;
}

.comment-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 1rem;
}

.comment-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  cursor: pointer;
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

.load-more-replies {
  padding: 1rem 0;
  text-align: center;
  border-top: 1px solid #eee;
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
