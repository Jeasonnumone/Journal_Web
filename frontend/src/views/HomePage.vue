<template>
  <main class="main-content">
    <div class="search-bar">
      <el-input v-model="keyword" @keyup.enter="search" placeholder="搜索期刊..." class="search-input" />
      <el-button type="primary" @click="search">搜索</el-button>
    </div>

    <div class="categories">
      <el-button 
        v-for="category in categories" 
        :key="category"
        @click="selectCategory(category)"
        :type="selectedCategory === category ? 'primary' : 'default'"
        round
      >
        {{ category }}
      </el-button>
    </div>

    <div class="journal-grid">
      <el-card 
        v-for="journal in journals" 
        :key="journal.id"
        @click="viewDetail(journal.id)"
        class="journal-card"
        shadow="hover"
      >
        <img :src="journal.cover || '/default-cover.jpg'" alt="封面" class="journal-cover" />
        <h3 class="journal-title">{{ journal.title }}</h3>
        <p class="journal-author">作者：{{ journal.author }}</p>
      </el-card>
    </div>

    <div v-if="totalPages > 1" class="pagination">
      <el-pagination
        v-model:current-page="pageNum"
        layout="prev, pager, next"
        :total="total"
        :page-size="itemsPerPage"
        @current-change="fetchJournals"
      />
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, getJournals } from '../api/index.js'

const router = useRouter()

const keyword = ref('')
const selectedCategory = ref('全部')
const categories = ref([])
const journals = ref([])
const pageNum = ref(1)
const itemsPerPage = ref(6)
const total = ref(0)
const totalPages = ref(0)

const fetchCategories = async () => {
  try {
    const { data } = await getCategories()
    categories.value = data.data
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

const fetchJournals = async () => {
  try {
    const { data } = await getJournals({
      keyword: keyword.value,
      category: selectedCategory.value,
      page: pageNum.value,
      size: itemsPerPage.value
    })
    const pageData = data.data
    journals.value = pageData.records
    total.value = pageData.total
    totalPages.value = Math.ceil(pageData.total / pageData.size)
  } catch (error) {
    console.error('获取期刊失败:', error)
  }
}

const search = () => {
  pageNum.value = 1
  fetchJournals()
}

const selectCategory = (category) => {
  selectedCategory.value = category
  pageNum.value = 1
  fetchJournals()
}

const viewDetail = (id) => {
  router.push(`/journal/${id}`)
}

onMounted(async () => {
  await fetchCategories()
  await fetchJournals()
})
</script>

<style scoped>
.main-content {
  flex: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.search-bar {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.search-input {
  flex: 1;
}

.categories {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
}

.journal-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.journal-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.journal-card:hover {
  transform: translateY(-5px);
}

.journal-cover {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 5px;
  margin-bottom: 1rem;
}

.journal-title {
  margin: 0 0 0.5rem 0;
  color: #333;
  font-size: 1.1rem;
}

.journal-author {
  color: #666;
  font-size: 0.85rem;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 2rem;
}
</style>
