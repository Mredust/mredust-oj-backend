package com.mredust.oj.model.enums.problem;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 题目提交语言枚举
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum ProblemSubmitLanguageEnum {
    
    JAVA("java", "java","class Solution {\n\n}"),
    PYTHON("python", "python","class Solution:");
    private final String language;
    
    private final String value;
    
    private final String template;
    
    
}
