package com.mredust.oj.config.redisson;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Slf4j
@Component
public class RedissonService {
    
    @Resource
    private RedissonClient redissonClient;
    
    public boolean handleRateLimit(String key) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 限流策略 流速率 限流时间间隔 限流时间
        boolean flag = rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        if (flag) {
            log.info("init rate = {}, interval = {}", rateLimiter.getConfig().getRate(), rateLimiter.getConfig().getRateInterval());
        }
        return rateLimiter.tryAcquire(1);
        
    }
}
