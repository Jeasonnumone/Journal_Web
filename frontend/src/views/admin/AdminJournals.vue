<template>
  <div class="admin-journals">
    <div class="toolbar">
      <el-input
        v-model="keyword"
        placeholder="搜索期刊名称"
        style="width: 300px"
        clearable
        @clear="fetchJournals"
        @keyup.enter="fetchJournals"
      >
        <template #append>
          <el-button @click="fetchJournals">搜索</el-button>
        </template>
      </el-input>
      <el-button type="primary" @click="showAddDialog">添加期刊</el-button>
    </div>

    <el-table :data="journals" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="jid" label="期刊ID" width="90" />
      <el-table-column prop="title" label="期刊名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="label" label="标签" width="120" />
      <el-table-column prop="organizer" label="主办单位" width="180" show-overflow-tooltip />
      <el-table-column prop="compositeImpactFactor" label="影响因子" width="100" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="showEditDialog(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="fetchJournals"
      />
    </div>

    <!-- 添加/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑期刊' : '添加期刊'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="期刊ID">
          <el-input v-model="form.jid" />
        </el-form-item>
        <el-form-item label="分类ID">
          <el-input v-model.number="form.typeid" />
        </el-form-item>
        <el-form-item label="期刊名称">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.label" />
        </el-form-item>
        <el-form-item label="封面URL">
          <el-input v-model="form.coverPath" />
        </el-form-item>
        <el-form-item label="主办单位">
          <el-input v-model="form.organizer" />
        </el-form-item>
        <el-form-item label="主管部门">
          <el-input v-model="form.department" />
        </el-form-item>
        <el-form-item label="复合影响因子">
          <el-input v-model="form.compositeImpactFactor" />
        </el-form-item>
        <el-form-item label="综合影响因子">
          <el-input v-model="form.comprehensiveImpactFactor" />
        </el-form-item>
        <el-form-item label="CN号">
          <el-input v-model="form.cnNumber" />
        </el-form-item>
        <el-form-item label="ISSN">
          <el-input v-model="form.issn" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.introduction" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminJournals, createJournal, updateJournal, deleteJournal } from '../../api/admin.js'
import { ElMessage, ElMessageBox } from 'element-plus'

const journals = ref([])
const loading = ref(false)
const keyword = ref('')
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const form = ref({})

const fetchJournals = async () => {
  loading.value = true
  try {
    const { data } = await getAdminJournals({
      page: currentPage.value,
      pageSize,
      keyword: keyword.value || undefined
    })
    journals.value = data.data.records
    total.value = data.data.total
  } catch (error) {
    console.error('获取期刊列表失败:', error)
  } finally {
    loading.value = false
  }
}

const showAddDialog = () => {
  isEdit.value = false
  editId.value = null
  form.value = {}
  dialogVisible.value = true
}

const showEditDialog = (row) => {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await updateJournal(editId.value, form.value)
      ElMessage.success('修改成功')
    } else {
      await createJournal(form.value)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchJournals()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除期刊 "${row.title}" 吗？`, '警告', { type: 'warning' })
    await deleteJournal(row.id)
    ElMessage.success('删除成功')
    fetchJournals()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除期刊失败:', error)
    }
  }
}

onMounted(fetchJournals)
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