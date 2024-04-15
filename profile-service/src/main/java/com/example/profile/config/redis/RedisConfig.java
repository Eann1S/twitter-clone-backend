package com.example.profile.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Getter
@Setter
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisConfig {

    public static final String FOLLOWERS_CACHE = "followers";
    public static final String FOLLOWEES_CACHE = "followees";
    public static final String FOLLOWEES_CELEBRITIES_CACHE = "followees_celebrities";

    private static final Duration CACHE_TIME_TO_LIVE = Duration.ofHours(12);
    private String host;
    private String port;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, Integer.parseInt(port));
        return new JedisConnectionFactory(configuration);
    }

    @Bean
    public RedisCacheConfiguration cacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(CACHE_TIME_TO_LIVE)
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
    }

    @Bean
    public RedisCacheManager redisCacheManager() {
        return RedisCacheManager.builder(jedisConnectionFactory())
                .cacheDefaults(cacheConfiguration())
                .build();
    }
}
