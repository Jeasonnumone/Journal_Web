<template>
  <div class="admin-comments">
    <el-table :data="comments" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="评论者" width="120" />
      <el-table-column prop="content" label="评论内容" min-width="250" show-overflow-tooltip />
      <el-table-column prop="journalTitle" label="所属期刊" width="300" show-overflow-tooltip />
      <el-table-column prop="isDeleted" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.isDeleted === 1 ? 'danger' : 'success'">
            {{ row.isDeleted === 1 ? '已删除' : '正常' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="评论时间" width="170">
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
        layout="prev, pager, next"
        @current-change="fetchComments"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminComments, adminDeleteComment } from '../../api/admin.js'
import { formatDateTime } from '../../utils/format.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const comments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)

const fetchComments = async () => {
  loading.value = true
  try {
    const { data } = await getAdminComments({
      page: currentPage.value,
      pageSize
    })
    comments.value = data.data.records
    total.value = data.data.total
  } catch (error) {
    console.error('获取评论列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该评论吗？', '警告', { type: 'warning' })
    await adminDeleteComment(row.id)
    ElMessage.success('删除成功')
    fetchComments()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除评论失败:', error)
    }
  }
}

onMounted(fetchComments)
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 1.5rem;
}
</style>