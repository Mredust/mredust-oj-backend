package com.mredust.oj.judge.codesandbox.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum LanguageEnum {
    JAVA("Java", "java"),
    
    PYTHON("Python3", "python3");
    
    private final String language;
    
    private final String value;
}
