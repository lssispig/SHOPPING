package com.java.shopping.service;

public interface OrderService {
    void createOrder(Long userId, Long productId, String orderNo);
    
    boolean payOrder(String orderNo, Long userId);
    
    boolean cancelOrder(String orderNo, Long userId);
    
    String getOrderStatus(String orderNo, Long userId);
}