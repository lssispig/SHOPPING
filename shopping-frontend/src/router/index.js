import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/seckill',
    name: 'Seckill',
    component: () => import('../views/Seckill.vue')
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('../views/Order.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
