package com.mredust.oj.utils;

import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;

/**
 * 异常抛出工具类
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class ThrowUtils {
    private ThrowUtils() {
    
    }
    
    public static void throwEx(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }
    
    public static void throwEx(boolean condition, ResponseCode responseCode) {
        throwEx(condition, new BusinessException(responseCode));
    }
    
    public static void throwEx(boolean condition, ResponseCode responseCode, String msg) {
        throwEx(condition, new BusinessException(responseCode, msg));
    }
}
