<template>
  <div class="admin-users">
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索用户名或邮箱"
        style="width: 300px"
        clearable
        @clear="fetchUsers"
        @keyup.enter="fetchUsers"
      >
        <template #append>
          <el-button @click="fetchUsers">搜索</el-button>
        </template>
      </el-input>
    </div>

    <el-table :data="users" v-loading="loading" stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" align="center" />
      <el-table-column prop="username" label="用户名" width="150" align="center" />
      <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
      <el-table-column prop="role" label="角色" width="120" align="center">
        <template #default="{ row }">
          <el-tag :type="roleTagType(row.role)">{{ roleLabel(row.role) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="注册时间" width="170" align="center">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" width="220" align="center">
        <template #default="{ row }">
          <el-select
            :model-value="row.role"
            size="small"
            style="width: 120px; margin-right: 8px"
            @change="(val) => handleRoleChange(row.id, val)"
          >
            <el-option label="普通用户" value="USER" />
            <el-option label="客服" value="SUPPORT" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchUsers"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminUsers, updateUserRole, deleteUser } from '../../api/admin.js'
import { formatDateTime } from '../../utils/format.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const loading = ref(false)
const keyword = ref('')
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)

const roleLabel = (role) => {
  const map = { USER: '普通用户', SUPPORT: '客服', ADMIN: '管理员' }
  return map[role] || role
}

const roleTagType = (role) => {
  const map = { USER: 'info', SUPPORT: 'warning', ADMIN: 'danger' }
  return map[role] || 'info'
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const { data } = await getAdminUsers({
      page: currentPage.value,
      pageSize,
      keyword: keyword.value || undefined
    })
    users.value = data.data.records
    total.value = data.data.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleRoleChange = async (id, role) => {
  try {
    await updateUserRole(id, role)
    ElMessage.success('角色修改成功')
    fetchUsers()
  } catch (error) {
    console.error('修改角色失败:', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除用户 "${row.username}" 吗？`, '警告', {
      type: 'warning'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除用户失败:', error)
    }
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.toolbar {
  margin-bottom: 1rem;
  display: flex;
  gap: 1rem;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 1.5rem;
}
</style>