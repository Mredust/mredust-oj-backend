package com.mredust.oj.model.dto.post;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class PostAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;
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
}
