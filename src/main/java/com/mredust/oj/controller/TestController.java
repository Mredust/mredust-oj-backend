package com.mredust.oj.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Api(tags = "测试接口")
@RestController
@RequestMapping("/test")
public class TestController {
    
    @ApiOperation("测试接口1")
    @GetMapping("/get")
    public String get() {
        return "success to get";
    }
    
    @ApiOperation("测试接口2")
    @GetMapping("/post")
    public String post() {
        return "success to post";
    }
}
