package com.mredust.oj.codesandbox.service.impl;

import com.mredust.oj.codesandbox.core.CodeSandboxFactory;
import com.mredust.oj.codesandbox.core.template.CodeSandboxTemplate;
import com.mredust.oj.codesandbox.model.dto.ExecuteCodeRequest;
import com.mredust.oj.codesandbox.model.dto.ExecuteCodeResponse;
import com.mredust.oj.codesandbox.model.enums.LanguageEnum;
import com.mredust.oj.codesandbox.service.CodeSandboxService;
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
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        String language = executeCodeRequest.getLanguage();
        if (StringUtils.isBlank(language)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL, "语言不能为空");
        }
        CodeSandboxTemplate codeSandboxTemplate = CodeSandboxFactory.getCodeSandboxTemplate(LanguageEnum.getLanguageEnum(language));
        return codeSandboxTemplate.executeCode(inputList, code);
    }
}
