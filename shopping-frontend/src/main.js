import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 屏蔽 ResizeObserver 误报警告（Element Plus 组件内部正常行为）
const errHandler = (e) => {
  if (e.message && e.message.includes('ResizeObserver')) {
    e.stopImmediatePropagation()
    return
  }
}
window.addEventListener('error', errHandler, true)

const app = createApp(App)
app.use(router)
app.use(ElementPlus)
app.mount('#app')
