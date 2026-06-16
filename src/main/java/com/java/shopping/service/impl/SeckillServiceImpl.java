package com.java.shopping.service.impl;

import com.google.common.util.concurrent.RateLimiter;
import com.java.shopping.entity.Product;
import com.java.shopping.mapper.ProductMapper;
import com.java.shopping.service.OrderService;
import com.java.shopping.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired(required = false)
    private KafkaTemplate<String, String> kafkaTemplate;

    private final RateLimiter rateLimiter = RateLimiter.create(1000.0);
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderService orderService;

    @Override
    public String seckill(Long userId, Long productId) throws Exception {
        //时间校验
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStock() <= 0){
            throw new Exception("商品不存在");
        }
        Date now = new Date();
        if(now.before(product.getStartTime())){
            throw new Exception("秒杀尚未开始");
        }
        if(now.after(product.getEndTime())){
            throw new Exception("秒杀已结束");
        }

        if (!rateLimiter.tryAcquire()) {
            throw new Exception("请求过于频繁，请稍后重试");
        }

        String userKey = "seckill:user:" + userId + ":" + productId;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(userKey))) {
            throw new Exception("您已抢购过该商品");
        }

        String stockKey = "seckill:stock:" + productId;
        Long remainingStock = redisTemplate.opsForValue().decrement(stockKey);

        if (remainingStock < 0) {
            redisTemplate.opsForValue().increment(stockKey);
            throw new Exception("商品已抢光");
        }

        redisTemplate.opsForValue().set(userKey, "1", 24, TimeUnit.HOURS);

        String orderNo = generateOrderNo();

        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("productId", productId);
        message.put("orderNo", orderNo);

        if (kafkaTemplate != null) {
            kafkaTemplate.send("seckill-topic", message.toString());
        } else {
            // Kafka未启用时直接同步创建订单
            try {
                orderService.createOrder(userId, productId, orderNo);
            } catch (Exception e) {
                // 订单创建失败，回滚Redis库存
                redisTemplate.opsForValue().increment(stockKey);
                redisTemplate.delete(userKey);
                throw new Exception("订单创建失败，请重试");
            }
        }

        return orderNo;
    }

    private String generateOrderNo() {
        return System.currentTimeMillis() + "" + String.format("%06d", new Random().nextInt(999999));
    }

    @Override
    public List<Map<String, Object>> listProducts() {
        List<Product> products = productMapper.selectList(null);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Product product : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getId());
            map.put("name", product.getName());
            map.put("price", product.getPrice());
            map.put("seckillPrice", product.getPrice());

            String stockKey = "seckill:stock:" + product.getId();
            String stockStr = redisTemplate.opsForValue().get(stockKey);
            int seckillStock = stockStr != null ? Integer.parseInt(stockStr) : product.getSeckillStock();
            map.put("stock", seckillStock);
            map.put("seckillStock", product.getSeckillStock());

            result.add(map);
        }
        return result;
    }
}