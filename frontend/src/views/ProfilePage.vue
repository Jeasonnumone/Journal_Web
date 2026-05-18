<template>
  <div class="profile-page">
    <div class="profile-container">
      <div class="profile-sidebar">
        <div class="sidebar-header">
          <div class="avatar-wrapper" @click="triggerUpload">
            <el-avatar :size="64" :src="userInfo?.avatar" class="user-avatar">
              <el-icon :size="32"><UserFilled /></el-icon>
            </el-avatar>
            <div class="avatar-overlay">
              <el-icon><Camera /></el-icon>
            </div>
          </div>
          <h3 class="username">{{ userInfo?.username || '加载中...' }}</h3>
        </div>
        
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="info">
            <el-icon><User /></el-icon>
            <span>个人信息</span>
          </el-menu-item>
          <el-menu-item index="posts">
            <el-icon><Document /></el-icon>
            <span>我的帖子</span>
          </el-menu-item>
          <el-menu-item index="comments">
            <el-icon><ChatDotRound /></el-icon>
            <span>我的评论</span>
          </el-menu-item>
          <el-menu-item index="password">
            <el-icon><Lock /></el-icon>
            <span>修改密码</span>
          </el-menu-item>
        </el-menu>
      </div>
      
      <div class="profile-content">
        <UserInfoCard v-if="activeMenu === 'info'" :user="userInfo" />
        <MyPosts v-else-if="activeMenu === 'posts'" />
        <MyComments v-else-if="activeMenu === 'comments'" />
        <ChangePassword v-else-if="activeMenu === 'password'" />
      </div>
    </div>

    <input 
      ref="fileInput" 
      type="file" 
      accept="image/*" 
      style="display: none" 
      @change="handleFileChange" 
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { UserFilled, User, Document, ChatDotRound, Lock, Camera } from '@element-plus/icons-vue'
import { getUserProfile, uploadAvatar } from '../api/index.js'
import { ElMessage } from 'element-plus'
import UserInfoCard from '../components/profile/UserInfoCard.vue'
import MyPosts from '../components/profile/MyPosts.vue'
import MyComments from '../components/profile/MyComments.vue'
import ChangePassword from '../components/profile/ChangePassword.vue'

const activeMenu = ref('info')
const userInfo = ref(null)
const fileInput = ref(null)
const uploading = ref(false)

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const triggerUpload = () => {
  if (uploading.value) return
  fileInput.value.click()
}

const handleFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }
  
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过2MB')
    return
  }
  
  uploading.value = true
  try {
    const { data } = await uploadAvatar(file)
    userInfo.value.avatar = data.data
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('上传头像失败:', error)
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

const fetchUserInfo = async () => {
  try {
    const { data } = await getUserProfile()
    userInfo.value = data.data
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 2rem;
}

.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 2rem;
  min-height: calc(100vh - 4rem);
}

.profile-sidebar {
  width: 250px;
  background: #fff;
  border-radius: 10px;
  padding: 1.5rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.sidebar-header {
  text-align: center;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
  margin-bottom: 1rem;
}

.avatar-wrapper {
  position: relative;
  display: inline-block;
  cursor: pointer;
  margin-bottom: 1rem;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.user-avatar {
  background-color: #e8e8e8;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 64px;
  height: 64px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: #fff;
  font-size: 20px;
}

.username {
  margin: 0;
  font-size: 1.1rem;
  color: #333;
  font-weight: 600;
}

.sidebar-menu {
  border-right: none;
}

.profile-content {
  flex: 1;
  background: #fff;
  border-radius: 10px;
  padding: 2rem;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}
</style>
