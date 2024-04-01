package com.example.profile.service.abstr;

import lombok.RequiredArgsConstructor;
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
class AbstractCacheServiceTest {

    @Mock
    private Cache cache;
    private AbstractCacheService abstractCacheService;

    @BeforeEach
    void setUp() {
        abstractCacheService = new TestCacheService(cache);
    }

    @ParameterizedTest
    @InstancioSource
    void getFromCache(String key, String value) {
        when(cache.get(key, String.class))
                .thenReturn(value);

        Optional<String> actualValue = abstractCacheService.getFromCache(key);

        assertThat(actualValue).contains(value);
    }

    @ParameterizedTest
    @InstancioSource
    void putInCache(String key, String value) {
        abstractCacheService.putInCache(key, value);

        verify(cache).put(key, GSON.toJson(value));
    }

    @ParameterizedTest
    @InstancioSource
    void evictFromCache(String key) {
        abstractCacheService.evictFromCache(key);

        verify(cache).evict(key);
    }

    @RequiredArgsConstructor
    static class TestCacheService extends AbstractCacheService {

        private final Cache cache;

        @Override
        protected Cache getCache() {
            return cache;
        }
    }
}