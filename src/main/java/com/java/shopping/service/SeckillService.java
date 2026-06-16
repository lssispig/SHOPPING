package com.java.shopping.service;

import java.util.List;
import java.util.Map;

public interface SeckillService {
    String seckill(Long userId, Long productId) throws Exception;
    List<Map<String, Object>> listProducts();
}