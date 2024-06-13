package com.mredust.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;


/**
 * 用户更新请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class UserUpdateMyRequest implements Serializable {
    
    /**
     * 用户昵称
     */
    private String username;
    
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 性别（0-女 1-男 2-未知）
     */
    private Integer sex;
    
    private static final long serialVersionUID = 1L;
}
