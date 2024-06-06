package com.mredust.oj.model.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@ApiModel(value = "用户注册请求")
@Data
public class UserRegisterRequest implements Serializable {
    
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
    
    /**
     * 确认密码
     */
    @ApiModelProperty(value = "确认密码", required = true)
    private String checkPassword;
}
