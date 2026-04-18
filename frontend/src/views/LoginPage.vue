<template>
  <div class="login-page">
    <div class="login-container">
      <button class="back-home-btn" @click="goToHome">← 返回首页</button>
      <h1 class="login-title">📚 德儒教育</h1>
      <h2 class="login-subtitle">登录</h2>
      
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input 
            v-model="loginForm.username" 
            type="text" 
            class="form-input" 
            placeholder="请输入用户名"
            required
          />
        </div>
        
        <div class="form-group">
          <label class="form-label">密码</label>
          <input 
            v-model="loginForm.password" 
            type="password" 
            class="form-input" 
            placeholder="请输入密码"
            required
          />
        </div>
        
        <div class="error-message" v-if="errorMessage">
          {{ errorMessage }}
        </div>
        
        <button type="submit" class="login-btn" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>
      
      <div class="login-footer">
        <span>还没有账号？</span>
        <a href="#" class="register-link" @click.prevent="goToRegister">去注册</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { login } from '../api'

const loginForm = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const errorMessage = ref('')

const emit = defineEmits(['login-success', 'go-to-register', 'go-to-home'])

const handleLogin = async () => {
  loading.value = true
  errorMessage.value = ''
  
  try {
    const result = await login(loginForm.value)
    
    if (result.code === 200) {
      localStorage.setItem('token', result.data.token)
      emit('login-success', result.data.user)
    } else {
      errorMessage.value = result.message || '登录失败'
    }
  } catch (error) {
    errorMessage.value = '登录失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const goToRegister = () => {
  emit('go-to-register')
}

const goToHome = () => {
  emit('go-to-home')
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-container {
  background: #fff;
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  position: relative;
}

.back-home-btn {
  position: absolute;
  top: 15px;
  left: 15px;
  padding: 6px 12px;
  font-size: 13px;
  border: 2px solid #ddd;
  border-radius: 5px;
  background-color: #fff;
  color: #666;
  cursor: pointer;
  transition: all 0.3s;
}

.back-home-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.login-title {
  text-align: center;
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
}

.login-subtitle {
  text-align: center;
  font-size: 24px;
  color: #666;
  margin-bottom: 30px;
  font-weight: normal;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  color: #333;
  font-weight: bold;
}

.form-input {
  padding: 12px 16px;
  font-size: 16px;
  border: 2px solid #ddd;
  border-radius: 10px;
  outline: none;
  transition: all 0.3s;
}

.form-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.error-message {
  color: #e74c3c;
  font-size: 14px;
  text-align: center;
  padding: 10px;
  background: #ffeaea;
  border-radius: 8px;
}

.login-btn {
  padding: 14px;
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.register-link {
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
  margin-left: 5px;
}

.register-link:hover {
  text-decoration: underline;
}
</style>