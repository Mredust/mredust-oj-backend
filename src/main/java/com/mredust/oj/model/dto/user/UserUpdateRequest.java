package com.mredust.oj.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * 用户更新请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@ApiModel(value = "更新用户信息请求", description = "用于管理员更新用户信息时的请求数据")
@Data
public class UserUpdateRequest implements Serializable {
    /**
     * id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private Long id;
    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", required = true)
    private String account;
    
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    /**
     * 用户昵称
     */
    @ApiModelProperty(value = "用户昵称")
    private String username;
    
    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String avatarUrl;
    /**
     * 性别（0-女 1-男 2-未知）
     */
    @ApiModelProperty(value = "性别")
    private Integer sex;
    
    /**
     * 账号状态（0-正常 1-封号）
     */
    @ApiModelProperty(value = "账号状态")
    private Integer status;
    
    /**
     * 用户角色（0-普通用户 1-管理员）
     */
    @ApiModelProperty(value = "用户角色")
    private Integer role;
    
    /**
     * 是否删除（0-未删除 1-已删除）
     */
    @ApiModelProperty(value = "是否删除")
    private Integer isDelete;
    
    private static final long serialVersionUID = 1L;
}
