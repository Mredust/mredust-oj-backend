package com.mredust.oj.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/get")
    public String get() {
        return "success to get";
    }
    
    @GetMapping("/post")
    public String post() {
        return "success to post";
    }
}
