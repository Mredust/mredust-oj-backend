package com.mredust.oj.exception;


import cn.dev33.satoken.exception.SaTokenException;
import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;


/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessException(BusinessException ex) {
        log.error("businessException：{}", ex.getMessage());
        return Result.fail(ResponseCode.FAIL, ex.getMessage());
    }
    
    
    @ExceptionHandler(SaTokenException.class)
    public BaseResponse handlerSaTokenException(SaTokenException e) {
        // 状态码细则：https://sa-token.cc/doc.html#/fun/exception-code?id=%e5%bc%82%e5%b8%b8%e7%bb%86%e5%88%86%e7%8a%b6%e6%80%81%e7%a0%81
        log.info(e.getMessage() + " ：" + e.getCode());
        List<Integer> unLoginCodes = new ArrayList<>(Arrays.asList(11001, 11002, 11003, 11011, 11012, 11013));
        if (unLoginCodes.contains(e.getCode())) {
            return Result.fail(ResponseCode.NOT_LOGIN);
        }
        if (e.getCode() == 11041) {
            return Result.fail(ResponseCode.NO_AUTH);
        }
        // 默认的提示
        return Result.fail(ResponseCode.FAIL, "服务器繁忙，请稍后重试...");
    }
    
    
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeException(RuntimeException ex) {
        log.error("runtimeException：{}", ex.getMessage());
        return Result.fail(ResponseCode.FAIL, "服务器繁忙，请稍后重试...");
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse handleValidationException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> constraintViolations = ex.getConstraintViolations();
        if (!constraintViolations.isEmpty()) {
            ConstraintViolation<?> violation = constraintViolations.iterator().next();
            String msg = String.format("参数%s", violation.getMessage());
            return Result.fail(ResponseCode.PARAMS_ERROR, msg);
        }
        return Result.fail(ResponseCode.PARAMS_ERROR);
    }
    
    @ExceptionHandler(BindException.class)
    public BaseResponse handleMethodVoArgumentNotValidException(BindException ex) {
        List<FieldError> fieldErrors = ex.getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            FieldError error = fieldErrors.get(0);
            String repStr = Objects.requireNonNull(error.getDefaultMessage()).replace("null", "空");
            String msg = String.format("参数%s", repStr);
            return Result.fail(ResponseCode.PARAMS_ERROR, msg);
        }
        return Result.fail(ResponseCode.PARAMS_ERROR);
    }
    
}

