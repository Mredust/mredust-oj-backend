package com.mredust.oj.codesandbox.core;


import com.mredust.oj.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.oj.codesandbox.core.template.JavaCodeSandbox;
import com.mredust.oj.codesandbox.core.template.PythonCodeSandbox;
import com.mredust.oj.codesandbox.model.enums.LanguageEnum;

import java.util.EnumMap;

/**
 * 代码沙箱工厂: 单例（饿汉式） + 工厂
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class CodeSandboxFactory {
    private CodeSandboxFactory() {
    }
    
    private static final EnumMap<LanguageEnum, CodeSandboxTemplate> CODE_SANDBOX_TEMPLATE_ENUM_MAP = new EnumMap<>(LanguageEnum.class);
    
    static {
        CODE_SANDBOX_TEMPLATE_ENUM_MAP.put(LanguageEnum.JAVA, new JavaCodeSandbox());
        CODE_SANDBOX_TEMPLATE_ENUM_MAP.put(LanguageEnum.PYTHON, new PythonCodeSandbox());
    }
    
    public static CodeSandboxTemplate getCodeSandboxTemplate(LanguageEnum languageEnum) {
        return CODE_SANDBOX_TEMPLATE_ENUM_MAP.get(languageEnum);
    }
}
