<template>
  <div class="app-container">
    <el-header class="app-header">
      <div class="header-content">
        <div class="logo" @click="goHome">
          <el-icon><ShoppingCart /></el-icon>
          <span>秒杀系统</span>
        </div>
        <el-menu :default-active="currentPath" mode="horizontal" class="nav-menu">
          <el-menu-item index="/" @click="goHome">
            <el-icon><HomeFilled /></el-icon>
            首页
          </el-menu-item>
          <el-menu-item index="/seckill" @click="goSeckill">
            <el-icon><Lightning /></el-icon>
            秒杀
          </el-menu-item>
          <el-menu-item index="/order" @click="goOrder">
            <el-icon><Ticket /></el-icon>
            订单
          </el-menu-item>
        </el-menu>
      </div>
    </el-header>
    
    <el-main class="app-main">
      <router-view />
    </el-main>
    
    <el-footer class="app-footer">
      <p>© 2024 秒杀系统 - 极速抢购体验</p>
    </el-footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ShoppingCart, HomeFilled, Lightning, Ticket } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const currentPath = computed(() => route.path)

const goHome = () => {
  router.push('/')
}

const goSeckill = () => {
  router.push('/seckill')
}

const goOrder = () => {
  router.push('/order')
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0 30px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
}

.logo el-icon {
  font-size: 28px;
}

.nav-menu {
  background: transparent;
}

.nav-menu .el-menu-item {
  color: white;
}

.nav-menu .el-menu-item:hover {
  background: rgba(255, 255, 255, 0.1);
}

.nav-menu .el-menu-item.is-active {
  background: rgba(255, 255, 255, 0.2);
}

.app-main {
  flex: 1;
  background: #f5f7fa;
}

.app-footer {
  text-align: center;
  padding: 20px;
  background: #fff;
  border-top: 1px solid #ebeef5;
}

.app-footer p {
  margin: 0;
  color: #909399;
}
</style>
