package com.java.shopping.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Kafka 条件配置：仅在 app.seckill.kafka-enabled=true 时才启用
 * 避免启动时 AdminClient 连接 Broker 干扰其他项目
 */
@Configuration
@EnableKafka
@ConditionalOnProperty(name = "app.seckill.kafka-enabled", havingValue = "true", matchIfMissing = false)
public class KafkaConfig {
}
