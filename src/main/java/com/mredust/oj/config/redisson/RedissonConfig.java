package com.mredust.oj.config.redisson;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@Configuration
@ConfigurationProperties("spring.redis")
public class RedissonConfig {
    private String host;
    private String port;
    private String password;
    
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(String.format("redis://%s:%s", host, port)).setPassword(password).setDatabase(2);
        return Redisson.create(config);
    }
}
