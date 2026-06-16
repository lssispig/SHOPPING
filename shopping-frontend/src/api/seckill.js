import axios from 'axios'

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000
})

const userId = 1001

export const getProducts = () => {
  return instance.get('/seckill/list')
}

export const seckill = (productId) => {
  return instance.post(`/seckill/${productId}`, {}, {
    headers: { userId }
  })
}

export const payOrder = (orderNo) => {
  return instance.post(`/seckill/pay/${orderNo}`, {}, {
    headers: { userId }
  })
}

export const cancelOrder = (orderNo) => {
  return instance.post(`/seckill/cancel/${orderNo}`, {}, {
    headers: { userId }
  })
}

export const getOrderStatus = (orderNo) => {
  return instance.get(`/seckill/status/${orderNo}`, {
    headers: { userId }
  })
}

export default instance
