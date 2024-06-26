package com.mredust.oj.model.dto.post;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 帖子收藏 / 取消收藏请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@ApiModel
public class PostFavourAddRequest implements Serializable {
    
    /**
     * 帖子 id
     */
    private Long postId;
    
    private static final long serialVersionUID = 1L;
}
