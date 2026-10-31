package org.example.ticketSalesSystem.Config;

import org.springframework.cache.CacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory) {

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
}