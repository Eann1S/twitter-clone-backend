package com.example.profile.config.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    static final String PROFILES_CACHE = "profiles";

    private final CacheManager cacheManager;

    @Bean
    @Qualifier("profiles")
    public Cache profilesCache() {
        return cacheManager.getCache(PROFILES_CACHE);
    }
}
