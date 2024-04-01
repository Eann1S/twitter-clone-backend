package com.example.profile.service.abstr;

import com.example.profile.service.CacheService;
import com.google.gson.reflect.TypeToken;
import org.springframework.cache.Cache;

import java.util.Optional;

import static com.example.profile.config.gson.GsonConfig.GSON;

public abstract class AbstractCacheService implements CacheService {

    protected abstract Cache getCache();

    @Override
    public <T> Optional<T> getFromCache(String key) {
        Cache cache = getCache();
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
        Cache cache = getCache();
        if (cache != null) {
            String json = GSON.toJson(value);
            cache.put(key, json);
        }
    }

    @Override
    public void evictFromCache(String key) {
        Cache cache = getCache();
        if (cache != null) {
            cache.evict(key);
        }
    }
}
