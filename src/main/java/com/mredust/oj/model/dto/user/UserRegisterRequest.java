package com.mredust.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class UserRegisterRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 账号
     */
    private String account;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 确认密码
     */
    private String checkPassword;
}
