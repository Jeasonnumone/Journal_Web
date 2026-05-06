import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '../views/HomePage.vue'
import JournalDetail from '../views/JournalDetail.vue'
import LoginPage from '../views/LoginPage.vue'
import RegisterPage from '../views/RegisterPage.vue'
import PostDetail from '../views/PostDetail.vue'
import PublishPost from '../views/PublishPost.vue'
import EditPost from '../views/EditPost.vue'
import ProfilePage from '../views/ProfilePage.vue'

const routes = [
  { path: '/', component: HomePage },
  { path: '/journal/:id', component: JournalDetail },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage },
  { path: '/posts/publish', component: PublishPost, meta: { requiresAuth: true } },
  { path: '/posts/:id', component: PostDetail },
  { path: '/posts/:id/edit', component: EditPost, meta: { requiresAuth: true } },
  { path: '/profile', component: ProfilePage, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken')
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
