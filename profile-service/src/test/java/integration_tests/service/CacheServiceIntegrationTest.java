package integration_tests.service;

import com.example.profile.ProfileServiceApplication;
import com.example.profile.service.CacheService;
import com.example.profile.service.impl.CacheServiceImpl;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import test_util.annotation.DisableDatabaseAutoConfiguration;
import test_util.starter.ConfigServerStarter;
import test_util.starter.RedisStarter;

import java.util.Optional;

import static com.example.profile.config.gson.GsonConfig.GSON;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;


@SpringBootTest(classes = {
        ProfileServiceApplication.class,
        CacheServiceIntegrationTest.IntegrationTestCacheConfig.class
})
@ActiveProfiles("test")
@DisableDatabaseAutoConfiguration
@ExtendWith({InstancioExtension.class})
public class CacheServiceIntegrationTest implements RedisStarter, ConfigServerStarter {

    static final String TEST_CACHE_NAME = "test";

    @Autowired
    @Qualifier("test")
    private Cache cache;
    private CacheService cacheService;

    @BeforeEach
    public void setUp() {
        this.cache = spy(cache);
        cacheService = new CacheServiceImpl(cache);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldPutValueInCache(String key, String value) {
        cacheService.putInCache(key, value);

        verify(cache).put(key, GSON.toJson(value));
    }

    @ParameterizedTest
    @InstancioSource
    void shouldReturnValueFromCache(String key, String value) {
        cacheService.putInCache(key, value);

        Optional<String> result = cacheService.getFromCache(key);

        assertThat(result).contains(value);
    }

    @ParameterizedTest
    @InstancioSource
    void shouldEvictValueFromCache(String key, String value) {
        cacheService.putInCache(key, value);

        cacheService.evictFromCache(key);

        verify(cache).evict(key);
    }

    @TestConfiguration
    static class IntegrationTestCacheConfig {

        @Autowired
        private CacheManager cacheManager;

        @Bean
        @Qualifier("test")
        public Cache testCache() {
            return cacheManager.getCache(TEST_CACHE_NAME);
        }
    }
}
