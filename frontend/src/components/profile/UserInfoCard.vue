<template>
  <div class="user-info-card">
    <h3 class="section-title">个人信息</h3>
    
    <div class="info-list" v-if="user">
      <div class="info-item">
        <span class="info-label">头像</span>
        <span class="info-value">
          <el-avatar :size="48" :src="user.avatar">
            <el-icon :size="24"><UserFilled /></el-icon>
          </el-avatar>
        </span>
      </div>
      <div class="info-item">
        <span class="info-label">用户名</span>
        <span class="info-value">{{ user.username }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">邮箱</span>
        <span class="info-value">{{ user.email }}</span>
      </div>
      <div class="info-item">
        <span class="info-label">角色</span>
        <span class="info-value">
          <el-tag :type="user.role === 'ADMIN' ? 'danger' : 'success'">
            {{ user.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </span>
      </div>
      <div class="info-item">
        <span class="info-label">注册时间</span>
        <span class="info-value">{{ formatDateTime(user.createTime) }}</span>
      </div>
    </div>
    
    <el-skeleton v-else :rows="4" animated />
  </div>
</template>

<script setup>
import { UserFilled } from '@element-plus/icons-vue'
import { formatDateTime } from '../../utils/format.js'

defineProps({
  user: {
    type: Object,
    default: null
  }
})
</script>

<style scoped>
.user-info-card {
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

.info-list {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-label {
  width: 120px;
  color: #666;
  font-weight: 500;
}

.info-value {
  flex: 1;
  color: #333;
}
</style>
