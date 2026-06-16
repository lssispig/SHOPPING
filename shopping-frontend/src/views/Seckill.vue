<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="seckill-page-container">
    <el-card class="seckill-header" shadow="hover">
      <div class="header-content">
        <div class="countdown">
          <el-icon><Clock /></el-icon>
          <span class="countdown-text">距离秒杀开始还有</span>
          <el-tag type="danger" class="countdown-tag">
            {{ formatTime(remainingTime) }}
          </el-tag>
        </div>
      </div>
    </el-card>
    
    <el-row :gutter="20">
      <el-col :span="18">
        <div class="product-list">
          <el-card 
            v-for="product in products" 
            :key="product.id" 
            class="product-card"
            shadow="hover">
            <div class="product-content">
              <el-image 
                :src="product.image" 
                fit="cover" 
                class="product-image">
              </el-image>
              <div class="product-info">
                <h3 class="product-name">{{ product.name }}</h3>
                <p class="product-desc">{{ product.description }}</p>
                <div class="product-price">
                  <span class="seckill-price">¥{{ product.seckillPrice }}</span>
                </div>
                <div class="product-stock">
                  <el-progress 
                    :percentage="product.seckillStock ? Math.round(product.stock / product.seckillStock * 100) : 0" 
                    :status="product.stock > 3 ? 'success' : (product.stock > 0 ? 'warning' : 'exception')">
                  </el-progress>
                  <span class="stock-text">剩余库存: {{ product.stock }}件</span>
                </div>
                <el-button 
                  :type="product.stock > 0 && canSeckill ? 'primary' : 'disabled'"
                  :disabled="product.stock <= 0 || !canSeckill"
                  :loading="seckillingId === product.id"
                  class="seckill-btn"
                  @click="handleSeckill(product.id)">
                  <el-icon><Lightning /></el-icon>
                  {{ product.stock <= 0 ? '已抢光' : (canSeckill ? '立即抢购' : '秒杀未开始') }}
                </el-button>
              </div>
            </div>
          </el-card>
        </div>
      </el-col>
      
      <el-col :span="6">
        <el-card class="side-card" shadow="hover">
          <template #header>
            <span>秒杀攻略</span>
          </template>
          <ul class="strategy-list">
            <li><el-icon><Check /></el-icon> 提前登录账号</li>
            <li><el-icon><Check /></el-icon> 确认收货地址</li>
            <li><el-icon><Check /></el-icon> 保持网络畅通</li>
            <li><el-icon><Check /></el-icon> 提前进入商品页面</li>
            <li><el-icon><Check /></el-icon> 秒杀开始后立即点击</li>
          </ul>
        </el-card>
        
        <el-card class="side-card" shadow="hover">
          <template #header>
            <span>我的秒杀记录</span>
          </template>
          <div v-if="seckillRecords.length === 0" class="empty-record">
            <el-icon><ShoppingBag /></el-icon>
            <p>暂无秒杀记录</p>
          </div>
          <div v-else class="record-list">
            <div v-for="record in seckillRecords" :key="record.orderNo" class="record-item">
              <span class="record-product">{{ record.productName }}</span>
              <span class="record-status" :class="record.statusClass">{{ record.status }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-dialog title="秒杀结果" :visible="showResult" @close="showResult = false">
      <div class="result-content" :class="resultSuccess ? 'success' : 'error'">
        <el-icon :size="64"><component :is="resultSuccess ? Check : Close" /></el-icon>
        <h3>{{ resultMessage }}</h3>
        <div v-if="resultSuccess && resultOrderNo" class="order-info">
          <p>订单号：{{ resultOrderNo }}</p>
          <el-button type="primary" @click="goToOrder">查看订单</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>import { ref, onMounted, onUnmounted } from 'vue';
import { Clock, Lightning, Check, ShoppingBag, Close } from '@element-plus/icons-vue';
import { seckill, getProducts } from '../api/seckill';
import { useRouter } from 'vue-router';
const router = useRouter();

const productImages = {
  1: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20smartphone%20product%20photo&image_size=portrait_4_3',
  2: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MacBook%20Pro%20laptop%20product%20photo&image_size=portrait_4_3',
  3: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=AirPods%20Pro%20earbuds%20product%20photo&image_size=portrait_4_3',
  4: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPad%20Pro%20tablet%20product%20photo&image_size=portrait_4_3'
};

const products = ref([]);
const remainingTime = ref(10000);
const canSeckill = ref(false);
const seckillingId = ref(null);
const showResult = ref(false);
const resultSuccess = ref(false);
const resultMessage = ref('');
const resultOrderNo = ref('');
const seckillRecords = ref([
 { productName: 'iPhone 15 Pro', status: '待支付', statusClass: 'pending' },
 { productName: 'AirPods Pro 2', status: '已支付', statusClass: 'paid' }
]);
let timer = null;
const formatTime = (ms) => {
 const hours = Math.floor(ms / (1000 * 60 * 60));
 const minutes = Math.floor((ms % (1000 * 60 * 60)) / (1000 * 60));
 const seconds = Math.floor((ms % (1000 * 60)) / 1000);
 return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
};

const getDescription = (id) => {
  const descs = {
    1: 'A17 Pro芯片，钛金属设计，专业级相机系统',
    2: 'M3 Pro芯片，Liquid Retina XDR显示屏',
    3: '主动降噪，个性化空间音频',
    4: 'M2芯片，Liquid Retina XDR显示屏'
  };
  return descs[id] || '热门秒杀商品';
};
const handleSeckill = async (productId) => {
 seckillingId.value = productId;
 try {
 const response = await seckill(productId);
 if (response.data.success) {
 resultSuccess.value = true;
 resultMessage.value = response.data.message;
 resultOrderNo.value = response.data.orderNo;
 const product = products.value.find(p => p.id === productId);
 if (product && product.stock > 0) {
 product.stock--;
 }
 seckillRecords.value.unshift({
 productName: products.value.find(p => p.id === productId)?.name || '未知商品',
 status: '待支付',
 statusClass: 'pending'
 });
 }
 else {
 resultSuccess.value = false;
 resultMessage.value = response.data.message;
 }
 }
 catch (error) {
 resultSuccess.value = false;
 resultMessage.value = error.response?.data?.message || '秒杀失败，请重试';
 }
 finally {
 seckillingId.value = null;
 showResult.value = true;
 }
};
const goToOrder = () => {
 showResult.value = false;
 router.push('/order');
};
onMounted(async () => {
  try {
    const res = await getProducts();
    if (res.data) {
      products.value = res.data.map(p => ({
        ...p,
        image: productImages[p.id] || '',
        description: getDescription(p.id)
      }));
    }
  } catch (e) {
    console.error('加载商品失败', e);
  }
  timer = setInterval(() => {
 if (remainingTime.value > 0) {
 remainingTime.value -= 1000;
 }
 else {
 canSeckill.value = true;
 clearInterval(timer);
 }
 }, 1000);
 });
onUnmounted(() => {
 if (timer) {
 clearInterval(timer);
 }
});
</script>

<style scoped>
.seckill-page-container {
  padding: 30px;
  max-width: 1400px;
  margin: 0 auto;
}

.seckill-header {
  margin-bottom: 30px;
}

.header-content {
  display: flex;
  justify-content: center;
  align-items: center;
}

.countdown {
  display: flex;
  align-items: center;
  gap: 15px;
}

.countdown-text {
  font-size: 18px;
  color: #606266;
}

.countdown-tag {
  font-size: 24px;
  font-weight: bold;
  padding: 10px 30px;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-card {
  margin-bottom: 0;
}

.product-content {
  display: flex;
  gap: 20px;
}

.product-image {
  width: 200px;
  height: 200px;
  border-radius: 8px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.product-desc {
  color: #909399;
  margin-bottom: 15px;
}

.product-price {
  margin-bottom: 15px;
}

.seckill-price {
  font-size: 28px;
  font-weight: bold;
  color: #f56c6c;
  margin-right: 15px;
}

.original-price {
  font-size: 18px;
  color: #909399;
  text-decoration: line-through;
}

.product-stock {
  margin-bottom: 15px;
}

.stock-text {
  display: block;
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}

.seckill-btn {
  align-self: flex-start;
  padding: 12px 40px;
  font-size: 16px;
}

.side-card {
  margin-bottom: 20px;
}

.strategy-list {
  list-style: none;
  padding: 0;
}

.strategy-list li {
  padding: 10px 0;
  color: #606266;
}

.empty-record {
  text-align: center;
  padding: 30px 0;
  color: #909399;
}

.empty-record el-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.record-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.record-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
}

.record-product {
  font-size: 14px;
}

.record-status {
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 4px;
}

.record-status.pending {
  background: #e8f5e9;
  color: #67c23a;
}

.record-status.paid {
  background: #e3f2fd;
  color: #409eff;
}

.result-content {
  text-align: center;
  padding: 40px;
}

.result-content.success el-icon {
  color: #67c23a;
}

.result-content.error el-icon {
  color: #f56c6c;
}

.result-content h3 {
  margin-top: 20px;
  font-size: 20px;
}

.order-info {
  margin-top: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
}

.order-info p {
  margin-bottom: 15px;
  font-size: 16px;
}
</style>
