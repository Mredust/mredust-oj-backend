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
    
    JAVA("Java", "java"),
    CPLUSPLUS("C++", "cpp"),
    GOLANG("JavaScript", "javascript"),
    PYTHON("Python", "python");
    private final String text;
    
    private final String value;
}
