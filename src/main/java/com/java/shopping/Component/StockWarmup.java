package com.java.shopping.Component;

import com.java.shopping.entity.Product;
import com.java.shopping.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockWarmup implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(StockWarmup.class);

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            logger.info("开始初始化库存到Redis...");
            List<Product> products = productMapper.selectList(null);
            for (Product product : products) {
                String stockKey = "seckill:stock:" + product.getId();
                stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(product.getSeckillStock()));
                logger.info("商品ID: {}, 秒杀库存: {}", product.getId(), product.getSeckillStock());
            }
            logger.info("库存初始化完成!");
        } catch (Exception e) {
            logger.error("库存初始化失败，可能是数据库或Redis未启动: {}", e.getMessage());
            logger.warn("应用将继续启动，但库存需要手动初始化");
        }
    }
}
