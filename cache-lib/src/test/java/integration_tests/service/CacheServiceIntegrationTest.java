package integration_tests.service;

import com.example.cache.service.CacheService;
import com.example.utils.test.starter.RedisStarter;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@ExtendWith({InstancioExtension.class})
public class CacheServiceIntegrationTest implements RedisStarter {

    @Autowired
    private CacheService cacheService;

    @ParameterizedTest
    @InstancioSource
    void shouldPutValueInCache(String key, String value) {
        cacheService.putInCache(key, value);

        assertThat(cacheService.getFromCache(key)).contains(value);
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

        assertThat(cacheService.getFromCache(key)).isEmpty();
    }

    @SpringBootApplication(scanBasePackages = "com.example.cache")
    static class TestConfig {
    }

}
