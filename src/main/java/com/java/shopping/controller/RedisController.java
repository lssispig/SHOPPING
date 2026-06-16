package com.java.shopping.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Value("${server.port}")
    private Integer serverPort;
    
    @Value("${spring.application.name}")
    private String applicationName;
    
    @Value("${spring.data.redis.host}")
    private String redisHost;
    
    @Value("${spring.data.redis.port}")
    private Integer redisPort;
    
    @Value("${spring.data.redis.password:}")
    private String redisPassword;
    
    @Value("${spring.data.redis.timeout:10000}")
    private String redisTimeout;
    
    @Value("${spring.data.redis.lettuce.pool.max-active:8}")
    private Integer maxActive;
    
    @Value("${spring.data.redis.lettuce.pool.max-wait:-1}")
    private String maxWait;
    
    @Value("${spring.data.redis.lettuce.pool.max-idle:8}")
    private Integer maxIdle;
    
    @Value("${spring.data.redis.lettuce.pool.min-idle:0}")
    private Integer minIdle;

    @GetMapping("/config")
    public String getRedisConfig() {
        return "读取配置成功：<br/>"
                + "项目端口：" + serverPort + "<br/>"
                + "项目名称：" + applicationName + "<br/>"
                + "Redis地址：" + redisHost + "<br/>"
                + "Redis端口：" + redisPort + "<br/>"
                + "Redis密码：" + redisPassword + "<br/>"
                + "超时时间：" + redisTimeout + "<br/>"
                + "最大连接：" + maxActive + "<br/>"
                + "最大等待：" + maxWait + "<br/>"
                + "最大空闲：" + maxIdle + "<br/>"
                + "最小空闲：" + minIdle;
    }

    @GetMapping("/set")
    public String setRedis(){
        redisTemplate.opsForValue().set("name","张三");
        redisTemplate.opsForValue().set("age","20");
        return "Redis保存成功";
    }

    @GetMapping("/test")
    public Object test(){
        redisTemplate.opsForValue().set("token1","dafdsfada",1, TimeUnit.HOURS);
        return "过期时间设置成功";
    }

    @GetMapping("/get")
    public Object getRedis(){
        return redisTemplate.opsForValue().get("name");
    }

    @GetMapping("/del")
    public String deRedis(){
        redisTemplate.delete("name");
        return "删除成功";
    }

    @GetMapping("/test2")
    public String testRedis() {
        redisTemplate.opsForValue().set("java", "Spring Boot Redis Demo");
        return "Redis 测试完成";
    }

    @GetMapping("/hash/set")
    public String setHash(){
        redisTemplate.opsForHash().put("user:100","name","张四");
        redisTemplate.opsForHash().put("user:100","age","22");
        redisTemplate.opsForHash().put("user:100","gender","man");
        return "Hash保存成功";
    }

    @GetMapping("/hash/get")
    public Object getHash(){
        return redisTemplate.opsForHash().get("user:100","name");
    }

    @GetMapping("/hash/entries")
    public Map<Object,Object> getHashAll(){
        return redisTemplate.opsForHash().entries("user:100");
    }

    @GetMapping("/hash/del")
    public Long delHashField(){
        return redisTemplate.opsForHash().delete("user:100","gender");
    }
}
