package com.mredust.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 账号
     */
    private String account;
    
    /**
     * 密码
     */
    private String password;
}
