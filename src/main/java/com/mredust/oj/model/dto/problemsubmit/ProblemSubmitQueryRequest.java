package com.mredust.oj.model.dto.problemsubmit;

import com.mredust.oj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProblemSubmitQueryRequest extends PageRequest implements Serializable {
    /**
     * 编程语言
     */
    private String language;
    
    /**
     * 提交状态
     */
    private Integer status;
    
    /**
     * 提交状态
     */
    private String message;
    
    /**
     * 题目 id
     */
    private Long problemId;
    
    
    
    private static final long serialVersionUID = 1L;
}
