package com.mredust.oj.model.dto.problem;

import lombok.Data;

/**
 * 题目配置
 *
 * @author Mredust
 */
@Data
public class JudgeConfig {
    
    /**
     * 时间限制（ms）
     */
    private Long timeLimit;
    
    /**
     * 内存限制（KB）
     */
    private Long memoryLimit;
    
    /**
     * 堆栈限制（KB）
     */
    private Long stackLimit;
}
