<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="home-page-container">
    <el-card class="welcome-card" shadow="hover">
      <template #header>
        <div class="card-header">
          <span>欢迎来到秒杀系统</span>
        </div>
      </template>
      <div class="welcome-content">
        <el-image 
          src="https://neeko-copilot.bytedance.net/api/text_to_image?prompt=shopping%20seckill%20banner%20with%20sale%20tags%20and%20discounts&image_size=landscape_16_9" 
          fit="cover" 
          class="banner-image">
        </el-image>
        <div class="btn-group">
          <el-button type="primary" size="large" @click="goToSeckill">
            <el-icon><ShoppingCart /></el-icon>
            立即抢购
          </el-button>
          <el-button size="large" @click="goToOrder">
            <el-icon><Ticket /></el-icon>
            我的订单
          </el-button>
        </div>
      </div>
    </el-card>
    
    <div class="features">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-card class="feature-card" shadow="hover">
            <div class="feature-icon">
              <el-icon><Lightning /></el-icon>
            </div>
            <h3>极速秒杀</h3>
            <p>毫秒级响应，极速抢购体验</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card" shadow="hover">
            <div class="feature-icon">
              <el-icon><Lock /></el-icon>
            </div>
            <h3>安全可靠</h3>
            <p>分布式锁保证数据一致性</p>
          </el-card>
        </el-col>
        <el-col :span="8">
          <el-card class="feature-card" shadow="hover">
            <div class="feature-icon">
              <el-icon><Refresh /></el-icon>
            </div>
            <h3>实时更新</h3>
            <p>库存实时同步，订单状态即时查询</p>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ShoppingCart, Ticket, Lightning, Lock, Refresh } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const goToSeckill = () => {
  router.push('/seckill')
}

const goToOrder = () => {
  router.push('/order')
}
</script>

<style scoped>
.home-page-container {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 30px;
}

.card-header {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.welcome-content {
  text-align: center;
}

.banner-image {
  width: 100%;
  height: 300px;
  margin-bottom: 30px;
  border-radius: 8px;
}

.btn-group {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.features {
  margin-top: 20px;
}

.feature-card {
  text-align: center;
  padding: 20px;
}

.feature-icon {
  font-size: 48px;
  color: #409eff;
  margin-bottom: 15px;
}

.feature-card h3 {
  margin: 10px 0;
  font-size: 18px;
}

.feature-card p {
  color: #909399;
  font-size: 14px;
}
</style>
