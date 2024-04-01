package com.example.profile.service;

import java.util.Optional;

public interface CacheService {

    <T> Optional<T> getFromCache(String key);
    <T> void putInCache(String key, T value);
    void evictFromCache(String key);
}
