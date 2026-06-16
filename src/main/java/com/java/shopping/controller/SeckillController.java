package com.java.shopping.controller;

import com.java.shopping.service.OrderService;
import com.java.shopping.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/list")
    public ResponseEntity<List<Map<String, Object>>> listProducts() {
        return ResponseEntity.ok(seckillService.listProducts());
    }

    @PostMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> seckill(
            @PathVariable Long productId,
            @RequestHeader("userId") Long userId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String orderNo = seckillService.seckill(userId, productId);
            result.put("success", true);
            result.put("orderNo", orderNo);
            result.put("message", "秒杀成功，请等待订单处理");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/pay/{orderNo}")
    public ResponseEntity<Map<String, Object>> pay(
            @PathVariable String orderNo,
            @RequestHeader("userId") Long userId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = orderService.payOrder(orderNo, userId);
            result.put("success", success);
            result.put("message", success ? "支付成功" : "支付失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    @PostMapping("/cancel/{orderNo}")
    public ResponseEntity<Map<String, Object>> cancelOrder(
            @PathVariable String orderNo,
            @RequestHeader("userId") Long userId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            boolean success = orderService.cancelOrder(orderNo, userId);
            result.put("success", success);
            result.put("message", success ? "取消成功" : "取消失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/status/{orderNo}")
    public ResponseEntity<Map<String, Object>> getOrderStatus(
            @PathVariable String orderNo,
            @RequestHeader("userId") Long userId) {
        
        Map<String, Object> result = new HashMap<>();
        String status = orderService.getOrderStatus(orderNo, userId);
        result.put("orderNo", orderNo);
        result.put("status", status);
        
        return ResponseEntity.ok(result);
    }
}