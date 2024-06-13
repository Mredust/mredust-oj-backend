package com.mredust.oj.model.dto.problemsubmit;

import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * 用户提交信息
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class ProblemSubmitAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 题目 id
     */
    @Min(1)
    private Long problemId;
    /**
     * 编程语言
     */
    private String language;
    
    /**
     * 用户代码
     */
    private String code;
    
}
