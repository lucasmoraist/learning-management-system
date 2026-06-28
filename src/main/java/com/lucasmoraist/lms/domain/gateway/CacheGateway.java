package com.lucasmoraist.lms.domain.gateway;

public interface CacheGateway {

    Boolean setIfAbsent(String key, Object value, long timeoutInSeconds);
    void delete(String key);

}
