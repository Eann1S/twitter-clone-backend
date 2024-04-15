package com.example.profile.config.service;

import com.example.profile.service.CacheService;
import com.example.profile.service.impl.CacheServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CacheServiceConfig {

    @Bean
    @Qualifier("profiles")
    public CacheService profilesCacheService(@Qualifier("profiles") Cache cache) {
        return new CacheServiceImpl(cache);
    }
}
