<template>
  <div class="edit-post-page">
    <div class="post-form-container">
      <h2 class="page-title">编辑帖子</h2>
      
      <el-form :model="postForm" :rules="rules" ref="formRef" label-position="top">
        <el-form-item label="标题" prop="title">
          <el-input 
            v-model="postForm.title" 
            placeholder="请输入帖子标题（最多200字）"
            maxlength="200"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item label="内容" prop="content">
          <el-input 
            v-model="postForm.content" 
            type="textarea" 
            placeholder="请输入帖子内容..."
            :rows="30"
            maxlength="5000"
            show-word-limit
          />
        </el-form-item>
        
        <el-form-item>
          <div class="form-actions">
            <el-button @click="router.push(`/posts/${postId}`)">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              保存
            </el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPostById, updatePost } from '../api/index.js'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const postId = route.params.id

const postForm = ref({
  title: '',
  content: ''
})

const rules = {
  title: [
    { required: true, message: '请输入标题', trigger: 'blur' },
    { min: 1, max: 200, message: '标题长度在 1 到 200 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入内容', trigger: 'blur' },
    { min: 1, max: 5000, message: '内容长度在 1 到 5000 个字符', trigger: 'blur' }
  ]
}

const fetchPost = async () => {
  try {
    const { data } = await getPostById(postId)
    postForm.value.title = data.data.title
    postForm.value.content = data.data.content
  } catch (error) {
    console.error('获取帖子失败:', error)
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await updatePost(postId, postForm.value)
      ElMessage.success('保存成功')
      router.push(`/posts/${postId}`)
    } catch (error) {
      console.error('保存失败:', error)
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  fetchPost()
})
</script>

<style scoped>
.edit-post-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 2rem;
}

.post-form-container {
  max-width: 800px;
  margin: 0 auto;
  background: #fff;
  border-radius: 10px;
  padding: 2rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.page-title {
  margin: 0 0 2rem 0;
  font-size: 1.5rem;
  color: #333;
  font-weight: 600;
}

.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  width: 100%;
}
</style>
