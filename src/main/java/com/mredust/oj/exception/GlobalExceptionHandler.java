package com.mredust.oj.exception;


import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessException(BusinessException e) {
        log.error("businessException：{}", e.getMessage());
        return Result.fail(ResponseCode.FAIL, e.getMessage());
    }
    
    
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeException(RuntimeException e) {
        log.error("runtimeException：{}", e.getMessage());
        return Result.fail(ResponseCode.FAIL, "系统错误");
    }
    
}

