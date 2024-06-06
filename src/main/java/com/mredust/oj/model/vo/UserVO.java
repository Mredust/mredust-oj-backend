package com.mredust.oj.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@ApiModel(value = "用户脱敏信息")
@Data
public class UserVO implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private Long id;
    
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
    @ApiModelProperty(value = "性别（0-女 1-男 2-未知）")
    private Integer sex;
    
    /**
     * 角色（0-普通用户 1-管理员）
     */
    @ApiModelProperty(value = "角色（0-普通用户 1-管理员）")
    private Integer role;
    
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
    
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
    
    private static final long serialVersionUID = 1L;
}
