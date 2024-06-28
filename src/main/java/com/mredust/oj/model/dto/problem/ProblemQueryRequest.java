package com.mredust.oj.model.dto.problem;

import com.mredust.oj.common.PageRequest;
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
public class ProblemQueryRequest extends PageRequest implements Serializable {
    /**
     * 题目id
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 关键词
     */
    private String keyword;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 难度
     */
    private Integer difficulty;
    
    
    private static final long serialVersionUID = 1L;
}
