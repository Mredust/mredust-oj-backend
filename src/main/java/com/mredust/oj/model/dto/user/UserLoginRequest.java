package com.mredust.oj.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@ApiModel(value = "用户登录请求")
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
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
}
