package com.mredust.oj.model.dto.user;

import lombok.Data;

import java.io.Serializable;


/**
 * 用户更新请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    private Long id;
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
    
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 性别（0-女 1-男 2-未知）
     */
    private Integer sex;
    
    /**
     * 账号状态（0-正常 1-封号）
     */
    private Integer status;
    
    /**
     * 用户角色（0-普通用户 1-管理员）
     */
    private Integer role;
    
    /**
     * 是否删除（0-未删除 1-已删除）
     */
    private Integer isDelete;
    
    private static final long serialVersionUID = 1L;
}
