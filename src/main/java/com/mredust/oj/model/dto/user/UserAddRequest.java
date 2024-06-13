package com.mredust.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;


/**
 * 用户创建请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class UserAddRequest implements Serializable {
    
    /**
     * 账号
     */
    private String account;
    
    /**
     * 密码
     */
    private String password;
    
    
    /**
     * 用户昵称
     */
    private String username;
    
    
    private static final long serialVersionUID = 1L;
}
