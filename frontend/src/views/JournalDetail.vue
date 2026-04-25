<template>
  <main class="detail-page">
    <div class="detail-container">
      <el-button @click="router.back()" type="primary" plain class="back-btn">
        ← 返回列表
      </el-button>

      <div v-if="journal" class="detail-body">
        <div class="detail-left">
          <img :src="journal.cover || '/default-cover.jpg'" alt="封面" class="detail-cover" />
        </div>
        <div class="detail-right">
          <h2 class="detail-title">{{ journal.title }}</h2>
          <div class="detail-info">
            <p><strong>作者：</strong>{{ journal.author }}</p>
            <p><strong>出版社：</strong>{{ journal.publisher }}</p>
            <p><strong>ISSN：</strong>{{ journal.issn }}</p>
          </div>
          <div class="detail-description">
            <h3>简介</h3>
            <p>{{ journal.description }}</p>
          </div>
        </div>
      </div>

      <CommentSection 
        v-if="journalId" 
        :journal-id="journalId" 
        :current-user="currentUser"
      />
    </div>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { getJournalById } from '../api/index.js'
import { currentUser } from '../composables/useAuth.js'
import CommentSection from '../components/CommentSection.vue'

const route = useRoute()
const router = useRouter()

const journalId = ref(route.params.id)
const journal = ref(null)

const fetchJournal = async () => {
  try {
    const { data } = await getJournalById(journalId.value)
    journal.value = data.data
  } catch (error) {
    console.error('获取期刊详情失败:', error)
  }
}

onMounted(fetchJournal)
</script>

<style scoped>
.detail-page {
  flex: 1;
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.detail-container {
  background: white;
  border-radius: 10px;
  padding: 2rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.back-btn {
  margin-bottom: 1.5rem;
}

.detail-body {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
}

.detail-left {
  flex: 0 0 300px;
}

.detail-cover {
  width: 100%;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.detail-right {
  flex: 1;
}

.detail-title {
  margin: 0 0 1rem 0;
  color: #333;
  font-size: 1.8rem;
}

.detail-info {
  margin-bottom: 1.5rem;
}

.detail-info p {
  margin: 0.5rem 0;
  color: #666;
}

.detail-description h3 {
  margin-bottom: 0.75rem;
  color: #333;
}

.detail-description p {
  color: #666;
  line-height: 1.6;
}
</style>
