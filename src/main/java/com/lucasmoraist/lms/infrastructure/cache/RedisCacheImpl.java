package com.lucasmoraist.lms.infrastructure.cache;

import com.lucasmoraist.lms.domain.gateway.CacheGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class RedisCacheImpl implements CacheGateway {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Boolean setIfAbsent(String key, Object value, long timeoutInSeconds) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value);
        if (Boolean.TRUE.equals(result)) {
            redisTemplate.expire(key, Duration.ofSeconds(timeoutInSeconds));
        }
        return result;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

}
