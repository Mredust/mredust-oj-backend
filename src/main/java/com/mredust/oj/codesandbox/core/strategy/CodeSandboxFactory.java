package com.mredust.oj.codesandbox.core.strategy;


import com.mredust.oj.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.oj.codesandbox.model.enums.LanguageEnum;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface CodeSandboxFactory {
    CodeSandboxTemplate getCodeSandboxTemplate(LanguageEnum languageEnum);
}
