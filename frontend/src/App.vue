<template>
  <div id="app">
    <header class="header">
      <div class="header-content">
        <h1 class="logo" @click="router.push('/')" style="cursor: pointer">📚 德儒教育期刊系统</h1>
        <div class="user-info">
          <template v-if="currentUser">
            <span class="welcome">欢迎，{{ currentUser.username }}</span>
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

    <router-view />
    
    <Footer />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { currentUser, initUser, logout } from './composables/useAuth.js'
import Footer from './components/Footer.vue'

const router = useRouter()

const handleLogout = async () => {
  await logout()
  router.push('/')
}

onMounted(initUser)
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

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.welcome {
  margin-right: 1rem;
}
</style>
