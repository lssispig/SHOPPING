package com.java.shopping.service.impl;

import com.java.shopping.entity.Order;
import com.java.shopping.entity.Product;
import com.java.shopping.mapper.OrderMapper;
import com.java.shopping.mapper.ProductMapper;
import com.java.shopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    @Transactional
    public void createOrder(Long userId, Long productId, String orderNo) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStock() <= 0) {
            return;
        }

        int updateCount = productMapper.decreaseStock(productId);
        if (updateCount == 0) {
            return;
        }

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setProductId(productId);
        order.setPrice(product.getPrice());
        order.setStatus(1);
        order.setCreateTime(new Date());
        orderMapper.insert(order);
    }

    @Override
    public boolean payOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo, userId);
        if (order == null) {
            return false;
        }
        if (order.getStatus() != 1) {
            return false;
        }
        int result = orderMapper.updateOrderStatus(orderNo, 2);
        return result > 0;
    }

    @Override
    public boolean cancelOrder(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo, userId);
        if (order == null) {
            return false;
        }
        if (order.getStatus() != 1) {
            return false;
        }
        int result = orderMapper.updateOrderStatus(orderNo, 3);
        if (result > 0) {
            productMapper.increaseStock(order.getProductId());
            return true;
        }
        return false;
    }

    @Override
    public String getOrderStatus(String orderNo, Long userId) {
        Order order = orderMapper.selectByOrderNo(orderNo, userId);
        if (order == null) {
            return "订单不存在";
        }
        switch (order.getStatus()) {
            case 1: return "待支付";
            case 2: return "已支付";
            case 3: return "已取消";
            default: return "未知状态";
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Scheduled(fixedRate = 60000)
    public void cancelTimeoutOrders() {
        try {
            List<Order> timeoutOrders = orderMapper.selectTimeoutOrders();
            for (Order order : timeoutOrders) {
                orderMapper.updateStatus(order.getId(), 3);
                productMapper.increaseStock(order.getProductId());
            }
        } catch (Exception e) {
            // 数据库未就绪或表不存在时静默跳过，避免刷屏
        }
    }

    @Scheduled(fixedRate = 60000)
    public void syncExpiredSeckillStock() {
        try {
            List<Product> expiredList = productMapper.selectExpiredSeckill();
            for (Product p : expiredList) {
                String stockKey = "seckill:stock:" + p.getId();
                String remain = redisTemplate.opsForValue().get(stockKey);
                if (remain != null) {
                    int remaining = Integer.parseInt(remain);
                    if (remaining > 0) {
                        productMapper.increaseStockByAmount(p.getId(), remaining);
                    }
                    redisTemplate.delete(stockKey);
                }
                productMapper.markSeckillEnded(p.getId());
            }
        } catch (Exception e) {
            // 静默跳过
        }
    }
}