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
    
    JAVA("java", "java"),
    PYTHON("python", "python");
    private final String language;
    
    private final String value;
}
