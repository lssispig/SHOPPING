<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="order-page-container">
    <el-card class="order-header" shadow="hover">
      <template #header>
        <div class="header-content">
          <span>我的订单</span>
          <el-tabs v-model="activeTab" class="order-tabs">
            <el-tab-pane label="全部" name="all"></el-tab-pane>
            <el-tab-pane label="待支付" name="pending"></el-tab-pane>
            <el-tab-pane label="已支付" name="paid"></el-tab-pane>
            <el-tab-pane label="已取消" name="cancelled"></el-tab-pane>
          </el-tabs>
        </div>
      </template>
    </el-card>
    
    <div v-if="filteredOrders.length === 0" class="empty-orders">
      <el-icon><ShoppingBag /></el-icon>
      <p>暂无{{ tabName }}订单</p>
    </div>
    
    <div v-else class="order-list">
      <el-card 
        v-for="order in filteredOrders" 
        :key="order.id" 
        class="order-card"
        shadow="hover">
        <div class="order-header-info">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-status" :class="order.statusClass">{{ order.status }}</span>
        </div>
        
        <div class="order-items">
          <div class="order-item">
            <el-image :src="order.productImage" fit="cover" class="item-image"></el-image>
            <div class="item-info">
              <h4>{{ order.productName }}</h4>
              <p class="item-desc">{{ order.productDesc }}</p>
              <p class="item-price">¥{{ order.price }}</p>
            </div>
          </div>
        </div>
        
        <div class="order-footer">
          <div class="order-total">
            <span>实付金额：</span>
            <span class="total-price">¥{{ order.price }}</span>
          </div>
          <div class="order-actions">
            <el-button 
              v-if="order.status === '待支付'" 
              type="primary" 
              :loading="payingOrderNo === order.orderNo"
              @click="handlePay(order.orderNo)">
              <el-icon><CreditCard /></el-icon>
              立即支付
            </el-button>
            <el-button 
              v-if="order.status === '待支付'" 
              type="warning" 
              :loading="cancellingOrderNo === order.orderNo"
              @click="handleCancel(order.orderNo)">
              <el-icon><Close /></el-icon>
              取消订单
            </el-button>
            <el-button 
              v-if="order.status === '已支付'" 
              type="success" 
              disabled>
              <el-icon><Check /></el-icon>
              已支付
            </el-button>
            <el-button 
              v-if="order.status === '已取消'" 
              type="info" 
              disabled>
              <el-icon><Clock /></el-icon>
              已取消
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
    
    <el-dialog title="操作结果" :visible="showResult" @close="showResult = false">
      <div class="result-content" :class="resultSuccess ? 'success' : 'error'">
        <el-icon :size="64"><component :is="resultSuccess ? Check : Close" /></el-icon>
        <h3>{{ resultMessage }}</h3>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>import { ref, computed } from 'vue';
import { ShoppingBag, CreditCard, Close, Check } from '@element-plus/icons-vue';
import { payOrder, cancelOrder } from '../api/seckill';
const activeTab = ref('all');
const payingOrderNo = ref(null);
const cancellingOrderNo = ref(null);
const showResult = ref(false);
const resultSuccess = ref(false);
const resultMessage = ref('');
const orders = ref([
 {
 id: 1,
 orderNo: '1677654321000123',
 productName: 'iPhone 15 Pro',
 productDesc: 'A17 Pro芯片，钛金属设计',
 productImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=iPhone%2015%20Pro%20smartphone&image_size=portrait_4_3',
 price: 7999,
 status: '待支付',
 statusClass: 'pending'
 },
 {
 id: 2,
 orderNo: '1677654321000456',
 productName: 'AirPods Pro 2',
 productDesc: '主动降噪，个性化空间音频',
 productImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=AirPods%20Pro%20earbuds&image_size=portrait_4_3',
 price: 1699,
 status: '已支付',
 statusClass: 'paid'
 },
 {
 id: 3,
 orderNo: '1677654321000789',
 productName: 'MacBook Pro 14寸',
 productDesc: 'M3 Pro芯片，Liquid Retina XDR显示屏',
 productImage: 'https://neeko-copilot.bytedance.net/api/text_to_image?prompt=MacBook%20Pro%20laptop&image_size=portrait_4_3',
 price: 12999,
 status: '已取消',
 statusClass: 'cancelled'
 }
]);
const tabName = computed(() => {
 const names = {
 all: '',
 pending: '待支付',
 paid: '已支付',
 cancelled: '已取消'
 };
 return names[activeTab.value] || '';
});
const filteredOrders = computed(() => {
 if (activeTab.value === 'all') {
 return orders.value;
 }
 const statusMap = {
 pending: '待支付',
 paid: '已支付',
 cancelled: '已取消'
 };
 return orders.value.filter(order => order.status === statusMap[activeTab.value]);
});
const handlePay = async (orderNo) => {
 payingOrderNo.value = orderNo;
 try {
 const response = await payOrder(orderNo);
 if (response.data.success) {
 resultSuccess.value = true;
 resultMessage.value = '支付成功';
 const order = orders.value.find(o => o.orderNo === orderNo);
 if (order) {
 order.status = '已支付';
 order.statusClass = 'paid';
 }
 }
 else {
 resultSuccess.value = false;
 resultMessage.value = response.data.message;
 }
 }
 catch (error) {
 resultSuccess.value = false;
 resultMessage.value = error.response?.data?.message || '支付失败，请重试';
 }
 finally {
 payingOrderNo.value = null;
 showResult.value = true;
 }
};
const handleCancel = async (orderNo) => {
 cancellingOrderNo.value = orderNo;
 try {
 const response = await cancelOrder(orderNo);
 if (response.data.success) {
 resultSuccess.value = true;
 resultMessage.value = '取消成功';
 const order = orders.value.find(o => o.orderNo === orderNo);
 if (order) {
 order.status = '已取消';
 order.statusClass = 'cancelled';
 }
 }
 else {
 resultSuccess.value = false;
 resultMessage.value = response.data.message;
 }
 }
 catch (error) {
 resultSuccess.value = false;
 resultMessage.value = error.response?.data?.message || '取消失败，请重试';
 }
 finally {
 cancellingOrderNo.value = null;
 showResult.value = true;
 }
};
</script>

<style scoped>
.order-page-container {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.order-header {
  margin-bottom: 30px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
}

.order-tabs {
  font-size: 14px;
}

.empty-orders {
  text-align: center;
  padding: 100px 0;
  color: #909399;
}

.empty-orders el-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-orders p {
  font-size: 18px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  margin-bottom: 0;
}

.order-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f5f7fa;
}

.order-no {
  font-size: 14px;
  color: #606266;
}

.order-status {
  font-size: 14px;
  padding: 5px 15px;
  border-radius: 4px;
}

.order-status.pending {
  background: #e8f5e9;
  color: #67c23a;
}

.order-status.paid {
  background: #e3f2fd;
  color: #409eff;
}

.order-status.cancelled {
  background: #f5f5f5;
  color: #909399;
}

.order-items {
  margin-bottom: 20px;
}

.order-item {
  display: flex;
  gap: 20px;
}

.item-image {
  width: 150px;
  height: 150px;
  border-radius: 8px;
}

.item-info {
  flex: 1;
}

.item-info h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
}

.item-desc {
  color: #909399;
  font-size: 14px;
  margin-bottom: 10px;
}

.item-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #f5f7fa;
}

.order-total {
  font-size: 16px;
}

.total-price {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.order-actions {
  display: flex;
  gap: 10px;
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
</style>
