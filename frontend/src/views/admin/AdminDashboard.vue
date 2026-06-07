<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="4" v-for="item in statCards" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ background: item.color }">
            <el-icon :size="28"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ item.value }}</div>
            <div class="stat-label">{{ item.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAdminStats } from '../../api/admin.js'
import { User, Reading, ChatDotRound, Document, Checked } from '@element-plus/icons-vue'

const stats = ref({})

const statCards = computed(() => [
  { label: '用户总数', value: stats.value.totalUsers || 0, icon: User, color: '#409eff' },
  { label: '客服数量', value: stats.value.supportCount || 0, icon: Checked, color: '#e6a23c' },
  { label: '期刊总数', value: stats.value.totalJournals || 0, icon: Reading, color: '#67c23a' },
  { label: '评论总数', value: stats.value.totalComments || 0, icon: ChatDotRound, color: '#f56c6c' },
  { label: '帖子总数', value: stats.value.totalPosts || 0, icon: Document, color: '#909399' },
  { label: '管理员数', value: stats.value.adminCount || 0, icon: User, color: '#9b59b6' }
])

onMounted(async () => {
  try {
    const { data } = await getAdminStats()
    stats.value = data.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 0;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1.2rem;
  width: 100%;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #333;
}

.stat-label {
  font-size: 0.85rem;
  color: #999;
  margin-top: 0.2rem;
}
</style>