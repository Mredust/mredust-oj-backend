package com.mredust.oj.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目提交封装类
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class ProblemSubmitVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    private Long id;
    
    /**
     * 编程语言
     */
    private String language;
    
    /**
     * 用户代码
     */
    private String code;
    
    /**
     * 判题状态（0-待判题 1-判题中 2-成功 3-失败）
     */
    private Integer status;
    
    /**
     * 题目id
     */
    private Long problemId;
    
    /**
     * 创建用户id
     */
    private Long userId;
    
    
    /**
     * 判题信息
     */
    private String message;
    
    /**
     * 判题错误信息
     */
    private String errorMessage;
    
    /**
     * 运行时间（ms）
     */
    private Long runTime;
    
    /**
     * 内存限制（KB）
     */
    private Long runMemory;
    
    /**
     * 栈大小（KB）
     */
    private Long runStack;
    
    
}
