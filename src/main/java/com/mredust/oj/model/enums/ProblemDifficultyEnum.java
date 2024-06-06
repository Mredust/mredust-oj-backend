package com.mredust.oj.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 题目难度枚举
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum ProblemDifficultyEnum {
    
    EASY(0, "简单"),
    
    MEDIUM(1, "中等"),
    
    HARD(2, "困难");
    
    private final Integer code;
    
    private final String text;
    
    
}
