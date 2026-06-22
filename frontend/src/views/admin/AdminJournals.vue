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
      <el-button
        type="warning"
        :disabled="selectedJournals.length === 0"
        @click="showBatchReplaceDialog"
      >
        批量替换 ({{ selectedJournals.length }})
      </el-button>
    </div>

    <el-table
      :data="journals"
      v-loading="loading"
      stripe
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="jid" label="期刊ID" width="90" />
      <el-table-column prop="title" label="期刊名称" min-width="200" show-overflow-tooltip />
      <el-table-column prop="label" label="标签" width="600" />
      <el-table-column prop="organizer" label="主办单位" width="300" show-overflow-tooltip />
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
        layout="prev, pager, next"
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

    <!-- 批量替换对话框 -->
    <el-dialog v-model="batchReplaceDialogVisible" title="批量替换" width="1000px">
      <el-form :model="batchReplaceForm" label-width="100px">
        <el-form-item label="选中期刊">
          <span>{{ selectedJournals.length }} 条</span>
        </el-form-item>
        <el-form-item label="替换字段">
          <el-select v-model="batchReplaceForm.field" placeholder="请选择字段" style="width: 100%">
            <el-option label="标签" value="label" />
            <el-option label="主办单位" value="organizer" />
            <el-option label="主管部门" value="department" />
            <el-option label="简介" value="introduction" />
          </el-select>
        </el-form-item>
        <el-form-item label="查找内容">
          <el-input v-model="batchReplaceForm.searchValue" placeholder="要查找的内容" />
        </el-form-item>
        <el-form-item label="替换为">
          <el-input v-model="batchReplaceForm.replaceValue" placeholder="替换后的内容" />
        </el-form-item>
        <el-form-item>
          <el-alert type="info" :closable="false">
            将在选中的 {{ selectedJournals.length }} 条期刊的「{{ getFieldLabel(batchReplaceForm.field) }}」字段中，将「{{ batchReplaceForm.searchValue || '空' }}」替换为「{{ batchReplaceForm.replaceValue || '空' }}」
          </el-alert>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchReplaceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchReplace" :loading="batchReplaceLoading">确定替换</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getAdminJournals, createJournal, updateJournal, deleteJournal, batchReplaceJournals } from '../../api/admin.js'
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

// 批量替换相关
const selectedJournals = ref([])
const batchReplaceDialogVisible = ref(false)
const batchReplaceLoading = ref(false)
const batchReplaceForm = ref({
  field: 'label',
  searchValue: '',
  replaceValue: ''
})

const fieldLabels = {
  label: '标签',
  organizer: '主办单位',
  department: '主管部门',
  introduction: '简介'
}

const getFieldLabel = (field) => {
  return fieldLabels[field] || field
}

const handleSelectionChange = (selection) => {
  selectedJournals.value = selection
}

const showBatchReplaceDialog = () => {
  batchReplaceForm.value = {
    field: 'label',
    searchValue: '',
    replaceValue: ''
  }
  batchReplaceDialogVisible.value = true
}

const handleBatchReplace = async () => {
  if (!batchReplaceForm.value.field) {
    ElMessage.warning('请选择替换字段')
    return
  }
  if (!batchReplaceForm.value.searchValue) {
    ElMessage.warning('请输入查找内容')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要在 ${selectedJournals.value.length} 条期刊的「${getFieldLabel(batchReplaceForm.value.field)}」字段中，将「${batchReplaceForm.value.searchValue}」替换为「${batchReplaceForm.value.replaceValue}」吗？`,
      '确认批量替换',
      { type: 'warning' }
    )

    batchReplaceLoading.value = true
    const ids = selectedJournals.value.map(j => j.id)
    const { data } = await batchReplaceJournals({
      ids,
      field: batchReplaceForm.value.field,
      searchValue: batchReplaceForm.value.searchValue,
      replaceValue: batchReplaceForm.value.replaceValue
    })
    ElMessage.success(`成功替换 ${data.data} 条期刊`)
    batchReplaceDialogVisible.value = false
    selectedJournals.value = []
    fetchJournals()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量替换失败:', error)
      ElMessage.error('批量替换失败')
    }
  } finally {
    batchReplaceLoading.value = false
  }
}

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