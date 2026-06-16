package com.java.shopping.consumer;

import com.java.shopping.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(name = "app.seckill.kafka-enabled", havingValue = "true")
public class OrderConsumer {

    @Autowired
    private OrderService orderService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @KafkaListener(topics = "seckill-topic", groupId = "shopping-seckill-group")
    public void consume(String message, Acknowledgment ack) {
        try {
            Long userId = extractUserId(message);
            Long productId = extractProductId(message);
            String orderNo = extractOrderNo(message);

            String lockKey = "lock:order:" + orderNo;
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 30, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(locked)) {
                try {
                    orderService.createOrder(userId, productId, orderNo);
                } catch (Exception e) {
                    rollbackStock(productId, userId);
                } finally {
                    redisTemplate.delete(lockKey);
                }
            }
            ack.acknowledge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long extractUserId(String message) {
        int start = message.indexOf("userId=") + 7;
        int end = message.indexOf(",", start);
        return Long.parseLong(message.substring(start, end).trim());
    }

    private Long extractProductId(String message) {
        int start = message.indexOf("productId=") + 10;
        int end = message.indexOf(",", start);
        return Long.parseLong(message.substring(start, end).trim());
    }

    private String extractOrderNo(String message) {
        int start = message.indexOf("orderNo=") + 8;
        int end = message.indexOf("}", start);
        return message.substring(start, end).trim();
    }

    private void rollbackStock(Long productId, Long userId) {
        redisTemplate.opsForValue().increment("seckill:stock:" + productId);
        redisTemplate.delete("seckill:user:" + userId + ":" + productId);
    }
}