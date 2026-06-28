package com.lucasmoraist.lms.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucasmoraist.lms.domain.gateway.CacheGateway;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Component
public class RedisCacheImpl implements CacheGateway {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ModelMapper modelMapper;

    public RedisCacheImpl(RedisTemplate<String, Object> redisTemplate, ModelMapper modelMapper) {
        this.redisTemplate = redisTemplate;
        this.modelMapper = modelMapper;
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

    @Override
    public Optional<Object> get(String cacheKey, Class<?> clazzType) {
        Object value = redisTemplate.opsForValue().get(cacheKey);
        if (value == null) {
            return Optional.empty();
        } else if (clazzType.isInstance(value)) {
            return Optional.of(value);
        } else {
            try {
                Object deserializedValue = this.modelMapper.map(value, clazzType);
                return Optional.of(deserializedValue);
            } catch (IllegalArgumentException e) {
                log.error("Failed to deserialize value for key {}: {}", cacheKey, e.getMessage());
                return Optional.empty();
            }
        }
    }

    @Override
    public void set(String cacheKey, Object value, int ttlInSeconds) {
        if (ttlInSeconds > 0) {
            redisTemplate.opsForValue().set(cacheKey, value, Duration.ofSeconds(ttlInSeconds));
        } else {
            redisTemplate.opsForValue().set(cacheKey, value);
        }
    }

}
