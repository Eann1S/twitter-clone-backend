package com.example.profile.service.impl;

import com.example.profile.service.abstr.AbstractCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Qualifier("profiles")
@Service
@RequiredArgsConstructor
public class ProfileCacheServiceImpl extends AbstractCacheService {

    private static final String CACHE_NAME = "profiles";

    private final CacheManager cacheManager;

    @Override
    @Nullable
    protected Cache getCache() {
        return cacheManager.getCache(CACHE_NAME);
    }
}
