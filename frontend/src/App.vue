<template>
  <div id="app">
    <template v-if="!isAdminPage">
      <header class="header">
      <div class="header-content">
        <h1 class="logo" @click="router.push('/')" style="cursor: pointer">📚 期刊摆渡</h1>
        
        <div class="header-actions">
          <el-button type="primary" round size="big" @click="router.push('/comments')">
            期刊点评
          </el-button>
          <el-button type="success" round size="big" @click="router.push('/categories')">
            期刊大全
          </el-button>
          <el-button 
            v-if="currentUser" 
            type="warning" 
            round 
            size="big" 
            @click="router.push('/posts/publish')"
          >
            发表帖子
          </el-button>
        </div>

        <div class="user-info">
          <template v-if="currentUser">
            <el-avatar :size="32" :src="currentUser.avatar" class="header-avatar" />
            <span class="welcome">欢迎，{{ currentUser.username }}</span>
            <el-button v-if="currentUser.role === 'ADMIN'" type="danger" plain size="small" @click="router.push('/admin')">管理后台</el-button>
            <el-button type="warning" plain size="small" @click="router.push('/profile')">个人中心</el-button>
            <el-button type="primary" plain size="small" @click="handleLogout">退出登录</el-button>
          </template>
          <template v-else>
            <el-button type="primary" size="small" @click="router.push('/login')">登录</el-button>
            <el-button type="success" size="small" @click="router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </header>
    </template>

    <router-view />
    
    <template v-if="!isAdminPage">
      <Footer />
      <ChatBox />
    </template>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { currentUser, initUser, logout, refreshTokens } from './composables/useAuth.js'
import Footer from './components/Footer.vue'
import ChatBox from './components/ChatBox.vue'

const router = useRouter()
const route = useRoute()

const isAdminPage = computed(() => route.path.startsWith('/admin'))

const handleLogout = async () => {
  await logout()
  router.push('/')
}

onMounted(async () => {
  await refreshTokens()
  await initUser()
})
</script>

<style scoped>
#app {
  font-family: Arial, sans-serif;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  margin: 0;
  font-size: 1.5rem;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.welcome {
  margin-right: 1rem;
}

.header-avatar {
  cursor: pointer;
  background-color: #e8e8e8;
}
</style>