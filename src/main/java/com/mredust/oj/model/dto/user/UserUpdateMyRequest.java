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
@ApiModel(value = "更新用户信息请求", description = "用于用户更新自己信息时的请求数据")
@Data
public class UserUpdateMyRequest implements Serializable {
    
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
    
    private static final long serialVersionUID = 1L;
}
