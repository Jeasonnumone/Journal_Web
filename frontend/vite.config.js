import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
// export default defineConfig({
//   plugins: [vue()],
// })

// export default defineConfig({
//   plugins: [vue()],

//   server: {
//     host: '0.0.0.0',   // ⭐关键：允许外部访问
//     port: 5173,
//     strictPort: true,

//     // ⭐关键：允许 ngrok / 外部域名访问
//     allowedHosts: 'all',

//     // 可选：避免跨域问题
//     cors: true
//   }
// })

export default defineConfig({
  plugins: [vue()],

  server: {
    host: '0.0.0.0',
    port: 5173,
    strictPort: true,
    
    // 允许所有主机访问（ngrok 必需）
    allowedHosts: true,  // 改为 true 而不是 'all'
    
    // 允许跨域
    cors: {
      origin: '*',
      methods: ['GET', 'POST', 'PUT', 'DELETE', 'OPTIONS'],
      allowedHeaders: ['Content-Type', 'Authorization']
    },
    
    // 禁用 HMR 检查（可选，解决某些网络问题）
    hmr: {
      protocol: 'ws',
      host: 'localhost',
      port: 5173
    }
  }
})