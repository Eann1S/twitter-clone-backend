package com.example.cache.service.impl;

import com.example.cache.service.CacheService;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static com.example.utils.config.gson.GsonConfig.GSON;

@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final ValueOperations<String, String> valueOperations;

    @Override
    public <T> Optional<T> getFromCache(String key) {
        String jsonValue = valueOperations.get(key);
        T value = GSON.fromJson(jsonValue, new TypeToken<>() {
        });
        return Optional.ofNullable(value);
    }

    @Override
    public <T> void putInCache(String key, T value) {
        String json = GSON.toJson(value);
        valueOperations.set(key, json);
    }

    @Override
    public void evictFromCache(String key) {
        valueOperations.getAndDelete(key);
    }
}
