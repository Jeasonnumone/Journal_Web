import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import HomePage from '../views/HomePage.vue'
import JournalDetail from '../views/JournalDetail.vue'
import LoginPage from '../views/LoginPage.vue'
import RegisterPage from '../views/RegisterPage.vue'
import PostDetail from '../views/PostDetail.vue'
import PublishPost from '../views/PublishPost.vue'
import EditPost from '../views/EditPost.vue'
import ProfilePage from '../views/ProfilePage.vue'
import CategoryPage from '../views/CategoryPage.vue'
import CommentsPage from '../views/CommentsPage.vue'
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminDashboard from '../views/admin/AdminDashboard.vue'
import AdminUsers from '../views/admin/AdminUsers.vue'
import AdminJournals from '../views/admin/AdminJournals.vue'
import AdminComments from '../views/admin/AdminComments.vue'
import AdminPosts from '../views/admin/AdminPosts.vue'
import { currentUser } from '../composables/useAuth.js'

const routes = [
  { path: '/', component: HomePage },
  { path: '/journal/:id', component: JournalDetail },
  { path: '/login', component: LoginPage },
  { path: '/register', component: RegisterPage },
  { path: '/posts/publish', component: PublishPost, meta: { requiresAuth: true } },
  { path: '/posts/:id', component: PostDetail },
  { path: '/posts/:id/edit', component: EditPost, meta: { requiresAuth: true } },
  { path: '/profile', component: ProfilePage, meta: { requiresAuth: true } },
  { path: '/categories', component: CategoryPage },
  { path: '/comments', component: CommentsPage },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAdmin: true },
    children: [
      { path: '', component: AdminDashboard },
      { path: 'users', component: AdminUsers },
      { path: 'journals', component: AdminJournals },
      { path: 'comments', component: AdminComments },
      { path: 'posts', component: AdminPosts }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken')

  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }

  if (to.meta.requiresAdmin) {
    if (!token) {
      ElMessage.error('请先登录')
      next('/login')
      return
    }
    if (currentUser.value?.role !== 'ADMIN') {
      ElMessage.error('需要管理员权限')
      next('/')
      return
    }
  }

  next()
})

export default router