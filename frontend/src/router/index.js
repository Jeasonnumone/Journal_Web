import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import JournalDetail from '../views/JournalDetail.vue'
import LoginPage from '../views/LoginPage.vue'
import RegisterPage from '../views/RegisterPage.vue'

const routes = [
  { path: '/', component: HomePage },
  { path: '/journal/:id', component: JournalDetail },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
