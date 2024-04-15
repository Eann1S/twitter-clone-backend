package com.example.profile.service.impl;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;

import java.util.Optional;

import static com.example.profile.config.gson.GsonConfig.GSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class CacheServiceImplTest {

    @Mock
    private Cache cache;
    private CacheServiceImpl cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheServiceImpl(cache);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnFromCache(String key, String value) {
        when(cache.get(key, String.class))
                .thenReturn(value);

        Optional<String> result = cacheService.getFromCache(key);

        assertThat(result).contains(value);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldPutInCache(String key, String value) {
        cacheService.putInCache(key, value);

        verify(cache).put(key, GSON.toJson(value));
    }

    @ParameterizedTest
    @InstancioSource
    void shouldEvictFromCache(String key) {
        cacheService.evictFromCache(key);

        verify(cache).evict(key);
    }
}