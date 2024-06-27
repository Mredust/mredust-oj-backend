package com.mredust.oj.judge.codesandbox.core;


import com.mredust.oj.judge.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.oj.judge.codesandbox.model.enums.LanguageEnum;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface CodeSandboxFactory {
    CodeSandboxTemplate getCodeSandboxTemplate(LanguageEnum languageEnum);
}
