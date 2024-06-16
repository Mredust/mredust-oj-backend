package com.mredust.oj.exception;


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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;


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
    
    
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeException(RuntimeException ex) {
        log.error("runtimeException：{}", ex.getMessage());
        return Result.fail(ResponseCode.FAIL, "系统错误");
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

