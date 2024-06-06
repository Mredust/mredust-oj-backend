package com.mredust.oj.model.dto.problem;

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
@ApiModel(value = "查询题目请求")
public class ProblemQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;
    
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
     * 题目答案
     */
    private String answer;
    
    /**
     * 创建用户 id
     */
    private Long userId;
    
    private static final long serialVersionUID = 1L;
}
