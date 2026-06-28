package com.lucasmoraist.lms.infrastructure.config.redis;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.IOException;

@Slf4j
@Configuration
@Profile("default")
public class EmbeddedRedisConfig {

    private RedisServer redisServer;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @PostConstruct
    public void startRedis() throws IOException {
        this.redisServer = new RedisServer(redisPort);
        this.redisServer.start();
        log.info("====== Embedded Redis started with success in port {} ======", redisPort);
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        if (this.redisServer != null) {
            this.redisServer.stop();
            log.info("====== Embedded Redis stopped with success ======");
        }
    }

}
