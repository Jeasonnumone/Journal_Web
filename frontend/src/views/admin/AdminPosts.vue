<template>
  <div class="admin-posts">
    <el-table :data="posts" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="作者" width="120" />
      <el-table-column prop="title" label="帖子标题" min-width="200" show-overflow-tooltip />
      <el-table-column prop="viewCount" label="浏览量" width="90" />
      <el-table-column prop="isDeleted" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.isDeleted === 1 ? 'danger' : 'success'">
            {{ row.isDeleted === 1 ? '已删除' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="发布时间" width="170">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.isDeleted !== 1"
            type="danger"
            size="small"
            @click="handleDelete(row)"
          >
            删除
          </el-button>
          <el-tag v-else type="info" size="small">已处理</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchPosts"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminPosts, adminDeletePost } from '../../api/admin.js'
import { formatDateTime } from '../../utils/format.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const posts = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)

const fetchPosts = async () => {
  loading.value = true
  try {
    const { data } = await getAdminPosts({
      page: currentPage.value,
      pageSize
    })
    posts.value = data.data.records
    total.value = data.data.total
  } catch (error) {
    console.error('获取帖子列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除帖子 "${row.title}" 吗？`, '警告', { type: 'warning' })
    await adminDeletePost(row.id)
    ElMessage.success('删除成功')
    fetchPosts()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除帖子失败:', error)
    }
  }
}

onMounted(fetchPosts)
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 1.5rem;
}
</style>