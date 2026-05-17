<template>
  <main class="detail-page">
    <div class="detail-container">
      <div v-if="journal" class="detail-body">
        <div class="detail-header">
          <el-button @click="router.back()" type="primary" plain class="back-btn">
            ← 返回列表
          </el-button>
        </div>

        <h2 class="detail-title">{{ journal.title }}</h2>

        <div class="detail-content">
          <div class="detail-left">
            <img :src="journal.coverPath || '/default-cover.jpg'" alt="封面" class="detail-cover" />
            <div class="detail-info">
              <p v-if="journal.department"><strong>主管部门：</strong>{{ journal.department }}</p>
              <p v-if="journal.organizer"><strong>主办单位：</strong>{{ journal.organizer }}</p>
              <p v-if="journal.editorialOffice"><strong>编辑部：</strong>{{ journal.editorialOffice }}</p>
              <p v-if="journal.cnNumber"><strong>CN号：</strong>{{ journal.cnNumber }}</p>
              <p v-if="journal.issn"><strong>ISSN：</strong>{{ journal.issn }}</p>
              <p v-if="journal.compositeImpactFactor"><strong>复合影响因子：</strong>{{ journal.compositeImpactFactor }}</p>
              <p v-if="journal.comprehensiveImpactFactor"><strong>综合影响因子：</strong>{{ journal.comprehensiveImpactFactor }}</p>
              <p v-if="journal.price"><strong>定价：</strong>{{ journal.price }}</p>
              <p v-if="journal.postalCodeSubscription"><strong>邮发代码：</strong>{{ journal.postalCodeSubscription }}</p>
              <p v-if="journal.address"><strong>地址：</strong>{{ journal.address }}</p>
              <p v-if="journal.postalCode"><strong>邮编：</strong>{{ journal.postalCode }}</p>
              <p v-if="journal.phone"><strong>电话：</strong>{{ journal.phone }}</p>
              <p v-if="journal.email"><strong>邮箱：</strong>{{ journal.email }}</p>
              <p v-if="journal.website">
                <strong>网址：</strong><a :href="journal.website" target="_blank" class="journal-link">{{ journal.website }}</a>
              </p>
            </div>
          </div>
          <div class="detail-right">
            <div class="detail-description" v-if="journal.introduction">
              <h3>简介</h3>
              <p>{{ journal.introduction }}</p>
            </div>
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
  position: relative;
}

.detail-header {
  position: absolute;
  top: 1.5rem;
  left: 1.5rem;
  z-index: 10;
}

.back-btn {
  flex-shrink: 0;
}

.detail-title {
  margin: 3rem 0 2rem 0;
  color: #333;
  font-size: 1.8rem;
  text-align: center;
}

.detail-content {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
}

.detail-left {
  flex: 0 0 200px;
}

.detail-cover {
  width: 100%;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 1.5rem;
}

.detail-info {
  font-size: 0.9rem;
  text-align: left;
}

.detail-info p {
  margin: 0.4rem 0;
  color: #666;
  line-height: 1.5;
  text-align: left;
}

.journal-link {
  color: #409eff;
  text-decoration: none;
  word-break: break-all;
}

.journal-link:hover {
  text-decoration: underline;
}

.detail-right {
  flex: 1;
  min-width: 0;
}

.detail-description h3 {
  margin-bottom: 1rem;
  color: #333;
  font-size: 1.2rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #409eff;
}

.detail-description p {
  color: #666;
  line-height: 1.8;
  text-align: justify;
  text-indent: 2em;
}
</style>
