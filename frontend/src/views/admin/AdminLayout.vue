<template>
  <div class="admin-layout">
    <aside class="admin-sidebar">
      <div class="sidebar-header">
        <h2 @click="router.push('/admin')" style="cursor: pointer">管理后台</h2>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/admin" class="nav-item" exact-active-class="active">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据统计</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item" active-class="active">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/journals" class="nav-item" active-class="active">
          <el-icon><Reading /></el-icon>
          <span>期刊管理</span>
        </router-link>
        <router-link to="/admin/comments" class="nav-item" active-class="active">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论管理</span>
        </router-link>
        <router-link to="/admin/posts" class="nav-item" active-class="active">
          <el-icon><Document /></el-icon>
          <span>帖子管理</span>
        </router-link>
      </nav>
    </aside>
    
    <main class="admin-main">
      <header class="admin-header">
        <h3>{{ pageTitle }}</h3>
        <div class="header-user">
          <el-avatar :size="32" :src="currentUser?.avatar" />
          <span>{{ currentUser?.username }}</span>
          <!-- <el-button type="danger" plain size="small" @click="handleLogout">退出</el-button> -->
           <el-button type="primary" plain size="big" @click="router.push('/')">返回前台</el-button>
        </div>
      </header>

      <div class="admin-content">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { currentUser, logout } from '../../composables/useAuth.js'
import { DataAnalysis, User, Reading, ChatDotRound, Document } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const pageTitle = computed(() => {
  const map = {
    '/admin': '数据统计',
    '/admin/users': '用户管理',
    '/admin/journals': '期刊管理',
    '/admin/comments': '评论管理',
    '/admin/posts': '帖子管理'
  }
  return map[route.path] || '管理后台'
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.admin-sidebar {
  width: 220px;
  background: #304156;
  color: white;
  display: flex;
  flex-direction: column;
}

.sidebar-header {
  padding: 1.2rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-header h2 {
  margin: 0;
  font-size: 1.2rem;
  color: #409eff;
}

.sidebar-nav {
  flex: 1;
  padding: 0.5rem 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.85rem 1.2rem;
  color: #bfcbd9;
  text-decoration: none;
  transition: all 0.3s;
  font-size: 0.95rem;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.05);
  color: white;
}

.nav-item.active {
  background: #409eff;
  color: white;
}

.sidebar-footer {
  padding: 1rem 1.2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.admin-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background: white;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}

.admin-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #333;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #666;
  font-size: 0.9rem;
}

.admin-content {
  flex: 1;
  padding: 1.5rem;
  overflow-y: auto;
}
</style>