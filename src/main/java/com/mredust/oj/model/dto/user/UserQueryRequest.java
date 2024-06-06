package com.mredust.oj.model.dto.user;

import com.mredust.oj.common.PageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@ApiModel(value = "用户查询请求")
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private Long id;
    /**
     * 账号
     */
    @ApiModelProperty(value = "账号")
    private String account;
    
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码")
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
    
    
    private static final long serialVersionUID = 1L;
}
