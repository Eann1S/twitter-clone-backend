package com.example.cache.config;

import com.example.cache.service.CacheService;
import com.example.cache.service.impl.CacheServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@RequiredArgsConstructor
public class CacheServiceConfig {

    private final StringRedisTemplate redisTemplate;

    @Bean
    public CacheService cacheService() {
        return new CacheServiceImpl(redisTemplate.opsForValue());
    }
}
