<template>
  <main class="category-page">
    <PageHeader title="期刊大全" />

    <div class="category-layout">
      <div class="left-panel">
        <div class="panel-title">学科分类</div>
        <div class="parent-list">
          <div 
            v-for="parent in parentCategories" 
            :key="parent.typeid"
            class="parent-item"
            :class="{ active: selectedParentTypeid === parent.typeid }"
            @click="selectParent(parent)"
          >
            {{ parent.name }}
          </div>
        </div>
      </div>

      <div class="right-panel">
        <div class="right-top">
          <div class="panel-title">子分类</div>
          <div class="children-list" v-if="currentChildren.length > 0">
            <span 
              v-for="child in currentChildren" 
              :key="child.typeid"
              class="child-tag"
              :class="{ active: selectedChildTypeid === child.typeid }"
              @click="selectChild(child)"
            >
              {{ child.name }}
            </span>
          </div>
          <div class="empty-tip" v-else>请选择左侧学科分类</div>
        </div>

        <div class="right-bottom">
          <div class="panel-title">
            期刊列表
            <span class="journal-count" v-if="journals.length > 0">（共 {{ journalTotal }} 本）</span>
          </div>
          <div class="journal-list" v-if="journals.length > 0">
            <div 
              v-for="journal in journals" 
              :key="journal.id"
              class="journal-item"
              @click="goToDetail(journal.id)"
            >
              {{ truncateTitle(journal.title) }}
            </div>
          </div>
          <div class="empty-tip" v-else>暂无期刊数据</div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, getJournals } from '../api/index.js'
import PageHeader from '../components/PageHeader.vue'

const router = useRouter()

const categories = ref([])
const selectedParentTypeid = ref(null)
const selectedChildTypeid = ref(null)
const journals = ref([])
const journalTotal = ref(0)

const parentCategories = computed(() => {
  return categories.value.filter(c => c.parentId === null || c.parentId === 0)
})

const currentChildren = computed(() => {
  if (!selectedParentTypeid.value) return []
  return categories.value.filter(c => c.parentId === selectedParentTypeid.value)
})

const selectParent = (parent) => {
  selectedParentTypeid.value = parent.typeid
  const children = categories.value.filter(c => c.parentId === parent.typeid)
  if (children.length > 0) {
    selectedChildTypeid.value = children[0].typeid
    fetchJournalsByTypeid(children[0].typeid)
  } else {
    selectedChildTypeid.value = null
    fetchJournalsByTypeid(parent.typeid)
  }
}

const selectChild = (child) => {
  selectedChildTypeid.value = child.typeid
  fetchJournalsByTypeid(child.typeid)
}

const goToDetail = (id) => {
  router.push(`/journal/${id}`)
}

const fetchJournalsByTypeid = async (typeid) => {
  try {
    const { data } = await getJournals({ typeid, page: 1, size: 200 })
    const pageData = data.data
    journals.value = pageData.records || []
    journalTotal.value = pageData.total || 0
  } catch (error) {
    console.error('获取期刊失败:', error)
    journals.value = []
    journalTotal.value = 0
  }
}

const fetchCategories = async () => {
  try {
    const { data } = await getCategories()
    categories.value = data.data || []
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const truncateTitle = (title) => {
  if (!title) return ''
  const index = title.indexOf('（')
  if (index > 0) {
    return title.substring(0, index)
  }
  return title
}

onMounted(fetchCategories)
</script>

<style scoped>
.category-page {
  flex: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.category-layout {
  display: flex;
  gap: 1.5rem;
  min-height: 600px;
}

.left-panel {
  width: 200px;
  min-width: 200px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.panel-title {
  padding: 0.85rem 1rem;
  font-weight: 600;
  font-size: 0.95rem;
  color: #333;
  background: #fafafa;
  border-bottom: 1px solid #f0f0f0;
}

.parent-list {
}

.parent-item {
  padding: 0.75rem 1rem;
  font-size: 0.9rem;
  color: #333;
  cursor: pointer;
  border-bottom: 1px solid #f5f5f5;
  transition: all 0.2s;
}

.parent-item:last-child {
  border-bottom: none;
}

.parent-item:hover {
  background-color: #fff7e6;
  color: #d48806;
}

.parent-item.active {
  background-color: #fff7e6;
  color: #d48806;
  font-weight: 600;
  border-left: 3px solid #d48806;
}

.right-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.right-top,
.right-bottom {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.children-list {
  padding: 1rem;
  display: flex;
  flex-wrap: wrap;
  gap: 0.65rem;
}

.child-tag {
  padding: 0.3rem 0.75rem;
  border-radius: 4px;
  font-size: 0.88rem;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
  border: 1px solid #e8e8e8;
}

.child-tag:hover {
  color: #409eff;
  border-color: #409eff;
  background-color: #ecf5ff;
}

.child-tag.active {
  color: #409eff;
  border-color: #409eff;
  background-color: #ecf5ff;
  font-weight: 500;
}

.journal-count {
  font-weight: 400;
  font-size: 0.85rem;
  color: #999;
  margin-left: 0.5rem;
}

.journal-list {
  
  padding: 0.5rem 1rem 1rem;
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.5rem 0;
}

.journal-item {
  /* text-align: left; */
  padding: 0.35rem 0;
  font-size: 0.88rem;
  color: #555;
  cursor: pointer;
  transition: color 0.2s;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.journal-item:hover {
  color: #409eff;
}

.empty-tip {
  padding: 2rem 1rem;
  text-align: center;
  color: #bbb;
  font-size: 0.9rem;
}
</style>
