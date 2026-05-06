<template>
  <div class="change-password">
    <h3 class="section-title">修改密码</h3>
    
    <el-form :model="passwordForm" :rules="rules" ref="formRef" label-position="top" class="password-form">
      <el-form-item label="旧密码" prop="oldPassword">
        <el-input 
          v-model="passwordForm.oldPassword" 
          type="password" 
          placeholder="请输入旧密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="新密码" prop="newPassword">
        <el-input 
          v-model="passwordForm.newPassword" 
          type="password" 
          placeholder="请输入新密码（至少8位，包含字母和数字）"
          show-password
        />
      </el-form-item>
      
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input 
          v-model="passwordForm.confirmPassword" 
          type="password" 
          placeholder="请再次输入新密码"
          show-password
        />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          保存修改
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { changePassword } from '../../api/index.js'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const submitting = ref(false)

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入密码'))
  } else if (value.length < 8) {
    callback(new Error('密码长度不能少于8位'))
  } else if (!/[a-zA-Z]/.test(value) || !/\d/.test(value)) {
    callback(new Error('密码必须包含字母和数字'))
  } else {
    callback()
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  oldPassword: [
    { required: true, message: '请输入旧密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, validator: validatePassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitting.value = true
    try {
      await changePassword({
        oldPassword: passwordForm.value.oldPassword,
        newPassword: passwordForm.value.newPassword
      })
      ElMessage.success('密码修改成功')
      passwordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
    } catch (error) {
      console.error('修改密码失败:', error)
    } finally {
      submitting.value = false
    }
  })
}
</script>

<style scoped>
.change-password {
  padding: 1rem;
}

.section-title {
  margin: 0 0 1.5rem 0;
  font-size: 1.2rem;
  color: #333;
  font-weight: 600;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #409eff;
}

.password-form {
  max-width: 500px;
}
</style>
