<template>
  <div class="register-page">
    <div class="register-container">
      <button class="back-home-btn" @click="goToHome">← 返回首页</button>
      <h1 class="register-title">📚 德儒教育</h1>
      <h2 class="register-subtitle">注册</h2>
      
      <form class="register-form" @submit.prevent="handleRegister">
        <div class="form-group">
          <label class="form-label">用户名</label>
          <input 
            v-model="registerForm.username" 
            type="text" 
            class="form-input" 
            placeholder="请输入用户名"
            required
          />
        </div>
        
        <div class="form-group">
          <label class="form-label">邮箱</label>
          <input 
            v-model="registerForm.email" 
            type="email" 
            class="form-input" 
            placeholder="请输入邮箱"
            required
          />
        </div>
        
        <div class="form-group">
          <label class="form-label">密码</label>
          <input 
            v-model="registerForm.password" 
            type="password" 
            class="form-input" 
            placeholder="请输入密码"
            required
          />
        </div>
        
        <div class="form-group">
          <label class="form-label">确认密码</label>
          <input 
            v-model="registerForm.confirmPassword" 
            type="password" 
            class="form-input" 
            placeholder="请再次输入密码"
            required
          />
        </div>
        
        <div class="error-message" v-if="errorMessage">
          {{ errorMessage }}
        </div>
        
        <div class="success-message" v-if="successMessage">
          {{ successMessage }}
        </div>
        
        <button type="submit" class="register-btn" :disabled="loading">
          {{ loading ? '注册中...' : '注册' }}
        </button>
      </form>
      
      <div class="register-footer">
        <span>已有账号？</span>
        <a href="#" class="login-link" @click.prevent="goToLogin">去登录</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { register } from '../api'

const registerForm = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const loading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')

const emit = defineEmits(['register-success', 'go-to-login', 'go-to-home'])

const handleRegister = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    errorMessage.value = '两次输入的密码不一致'
    return
  }
  
  loading.value = true
  
  try {
    const result = await register({
      username: registerForm.value.username,
      email: registerForm.value.email,
      password: registerForm.value.password
    })
    
    if (result.code === 200) {
      successMessage.value = '注册成功，请登录'
      setTimeout(() => {
        emit('go-to-login')
      }, 1500)
    } else {
      errorMessage.value = result.message || '注册失败'
    }
  } catch (error) {
    errorMessage.value = '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  emit('go-to-login')
}

const goToHome = () => {
  emit('go-to-home')
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.register-container {
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

.register-title {
  text-align: center;
  font-size: 32px;
  color: #333;
  margin-bottom: 10px;
}

.register-subtitle {
  text-align: center;
  font-size: 24px;
  color: #666;
  margin-bottom: 30px;
  font-weight: normal;
}

.register-form {
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

.success-message {
  color: #27ae60;
  font-size: 14px;
  text-align: center;
  padding: 10px;
  background: #eafff1;
  border-radius: 8px;
}

.register-btn {
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

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.register-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.register-footer {
  text-align: center;
  margin-top: 20px;
  font-size: 14px;
  color: #666;
}

.login-link {
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
  margin-left: 5px;
}

.login-link:hover {
  text-decoration: underline;
}
</style>