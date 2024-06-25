package com.mredust.oj;

import cn.dev33.satoken.SaManager;
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
public class OjApplication {
    
    public static void main(String[] args) {
        System.out.println("启动成功，Sa-Token 配置如下：" + SaManager.getConfig());
        SpringApplication.run(OjApplication.class, args);
    }
    
}
