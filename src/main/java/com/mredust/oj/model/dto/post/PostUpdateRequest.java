package com.mredust.oj.model.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@ApiModel(value = "更新帖子请求")
public class PostUpdateRequest implements Serializable {
    
    /**
     * id
     */
    @ApiModelProperty(value = "id", required = true)
    private Long id;
    
    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;
    
    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private String content;
    
    /**
     * 标签列表
     */
    @ApiModelProperty(value = "标签列表")
    private List<String> tags;
    
    private static final long serialVersionUID = 1L;
}
