package com.mredust.oj;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Mredust
 */

@SpringBootApplication
@MapperScan("com.mredust.oj.mapper")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class InitApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(InitApplication.class, args);
    }
    
}
