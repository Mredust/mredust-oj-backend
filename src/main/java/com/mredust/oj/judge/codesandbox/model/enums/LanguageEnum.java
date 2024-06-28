package com.mredust.oj.judge.codesandbox.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum LanguageEnum {
    JAVA("Java", "java", "class Solution {\n\n}"),
    PYTHON("Python3", "python", "class Solution:"),
    C("C", "c", "");
    private final String language;
    
    private final String value;
    
    private final String template;
}
