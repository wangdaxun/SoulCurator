import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './style.css'
import App from './App.vue'
import router from './router'
import { setupAnalytics } from './composables/useAnalytics'


const app = createApp(App)

app.use(createPinia())
app.use(router)

// 设置分析服务（自动初始化）
// 这会：
// 1. 自动初始化分析服务
// 2. 设置错误跟踪
// 3. 设置页面浏览跟踪
// 4. 启用点击跟踪
setupAnalytics(app, router) 
app.mount('#app')
