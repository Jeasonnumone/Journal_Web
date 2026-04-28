<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="contact-info">
        <div class="contact-item">
          <el-icon><Phone /></el-icon>
          <span>138-0000-0000</span>
        </div>
        <div class="contact-item">
          <el-icon><ChatDotRound /></el-icon>
          <span>deru_edu</span>
        </div>
        <div class="contact-item">
          <el-icon><Message /></el-icon>
          <span>contact@deru.com</span>
        </div>
        <div class="contact-item">
          <el-icon><Location /></el-icon>
          <span>某某市某某区某某路123号</span>
        </div>
      </div>
       
      <div class="copyright">
        © 2026 德儒教育 版权所有
      </div>
    </div>
  </footer>
</template>

<script setup>
import { Phone, ChatDotRound, Message, Location } from '@element-plus/icons-vue'
</script>

<style scoped>
.footer {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2rem 2rem 1.5rem;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.contact-info {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 2rem;
  margin-bottom: 1.5rem;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.contact-item .el-icon {
  font-size: 1.1rem;
}

.copyright {
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 0.85rem;
  opacity: 0.9;
}

@media (max-width: 768px) {
  .contact-info {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>
