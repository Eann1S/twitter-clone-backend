package com.example.profile.service.impl;

import com.example.profile.service.CacheService;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

import java.util.Optional;

import static com.example.profile.config.gson.GsonConfig.GSON;

@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    @Nullable
    private final Cache cache;

    @Override
    public <T> Optional<T> getFromCache(String key) {
        if (cache != null) {
            String jsonValue = cache.get(key, String.class);
            T value = GSON.fromJson(jsonValue, new TypeToken<>() {});
            return Optional.ofNullable(value);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T> void putInCache(String key, T value) {
        if (cache != null) {
            String json = GSON.toJson(value);
            cache.put(key, json);
        }
    }

    @Override
    public void evictFromCache(String key) {
        if (cache != null) {
            cache.evict(key);
        }
    }
}
