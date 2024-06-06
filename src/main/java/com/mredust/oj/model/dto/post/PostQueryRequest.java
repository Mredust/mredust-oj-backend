package com.mredust.oj.model.dto.post;

import com.mredust.oj.common.PageRequest;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "查询帖子请求")
public class PostQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    
    private Long id;
    
    /**
     * 搜索词
     */
    private String searchText;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 创建用户 id
     */
    private Long userId;
    
    private static final long serialVersionUID = 1L;
}
