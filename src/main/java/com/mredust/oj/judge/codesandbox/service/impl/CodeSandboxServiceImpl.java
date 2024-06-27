package com.mredust.oj.judge.codesandbox.service.impl;

import com.mredust.oj.judge.codesandbox.core.SimpleCodeSandboxFactory;
import com.mredust.oj.judge.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.oj.judge.codesandbox.model.dto.ExecuteRequest;
import com.mredust.oj.judge.codesandbox.model.dto.ExecuteResponse;
import com.mredust.oj.judge.codesandbox.model.enums.LanguageEnum;
import com.mredust.oj.judge.codesandbox.service.CodeSandboxService;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Service
public class CodeSandboxServiceImpl implements CodeSandboxService {
    
    @Override
    public ExecuteResponse executeCode(ExecuteRequest executeCodeRequest) {
        List<String> testCaseList = executeCodeRequest.getTestCaseList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        if (StringUtils.isBlank(language)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL, "语言不能为空");
        }
        CodeSandboxTemplate codeSandboxTemplate = new SimpleCodeSandboxFactory().getCodeSandboxTemplate(LanguageEnum.valueOf(language.toUpperCase()));
        return codeSandboxTemplate.executeCode(code, testCaseList);
    }
}
