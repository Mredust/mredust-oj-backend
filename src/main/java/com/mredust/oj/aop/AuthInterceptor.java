package com.mredust.oj.aop;


import com.mredust.oj.annotation.AuthCheck;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.user.AccountStatusEnum;
import com.mredust.oj.model.enums.user.RoleEnum;
import com.mredust.oj.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;
    
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        
        if (requestAttributes != null) {
            request = ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        // 获取登录用户
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        Integer userRoleCode = loginUser.getRole();
        Integer userAccountStatusCode = loginUser.getStatus();
        String role = authCheck.role();
        RoleEnum roleEnum = RoleEnum.getRoleEnumTypeByValue(role);
        RoleEnum userRoleEnum = RoleEnum.getRoleEnumTypeByCode(userRoleCode);
        AccountStatusEnum accountStatusEnum = AccountStatusEnum.getAccountStatusEnumTypeByCode(userAccountStatusCode);
        if (roleEnum != null) {
            if (userRoleEnum == null) {
                throw new BusinessException(ResponseCode.NO_AUTH);
            }
            // 账号封号，拒绝访问
            if (AccountStatusEnum.BAN.equals(accountStatusEnum)) {
                throw new BusinessException(ResponseCode.ACCOUNT_BAN);
            }
            // 无管理员权限
            if (!(RoleEnum.ADMIN.equals(userRoleEnum) && roleEnum.equals(userRoleEnum) && AccountStatusEnum.NORMAL.equals(accountStatusEnum))) {
                throw new BusinessException(ResponseCode.NO_AUTH);
            }
        }
        return joinPoint.proceed();
    }
}
