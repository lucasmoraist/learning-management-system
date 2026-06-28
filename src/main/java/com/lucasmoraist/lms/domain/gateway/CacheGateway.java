package com.lucasmoraist.lms.domain.gateway;

import java.util.Optional;

public interface CacheGateway {

    Boolean setIfAbsent(String key, Object value, long timeoutInSeconds);
    void delete(String key);
    Optional<Object> get(String cacheKey, Class<?> clazzType);
    void set(String cacheKey, Object value, int ttlInSeconds);

}
