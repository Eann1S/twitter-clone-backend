package com.example.cache.service.impl;

import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static com.example.utils.config.gson.GsonConfig.GSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class CacheServiceImplTest {

    @Mock
    private ValueOperations<String, String> valueOperations;
    private CacheServiceImpl cacheService;

    @BeforeEach
    void setUp() {
        cacheService = new CacheServiceImpl(valueOperations);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnFromCache(String key, String value) {
        when(valueOperations.get(key))
                .thenReturn(value);

        Optional<String> result = cacheService.getFromCache(key);

        assertThat(result).contains(value);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldPutInCache(String key, String value) {
        cacheService.putInCache(key, value);

        verify(valueOperations).set(key, GSON.toJson(value));
    }

    @ParameterizedTest
    @InstancioSource
    void shouldEvictFromCache(String key) {
        cacheService.evictFromCache(key);

        verify(valueOperations).getAndDelete(key);
    }
}