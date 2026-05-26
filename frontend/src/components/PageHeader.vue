<template>
  <div class="page-header">
    <el-button text class="back-btn" @click="handleBack">
      <el-icon><ArrowLeft /></el-icon>
      {{ backText }}
    </el-button>
    <h1 class="page-title">{{ title }}</h1>
    <div class="header-right">
      <slot name="right"></slot>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  backPath: {
    type: String,
    default: '/'
  },
  backText: {
    type: String,
    default: '返回'
  }
})

const router = useRouter()

const handleBack = () => {
  router.push(props.backPath)
}
</script>

<style scoped>
 .page-header {
  position: relative;
  display: flex;
  align-items: center;
  height: 56px;
  padding: 0 16px;
  background: #d3deee;
  border-bottom: 1px solid #eee;
}

/* 左侧返回 */
.back-btn {
  position: absolute;
  left: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  color: #606266;
  font-size: 15px;
}

/* 标题真正居中 */
.page-title {
  margin: 0 auto;
  font-size: 28px;
  font-weight: 600;
  color: #747272;
}

/* 右侧区域 */
.header-right {
  position: absolute;
  right: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>