package com.mredust.oj.codesandbox.service;


import com.mredust.oj.codesandbox.model.dto.ExecuteCodeRequest;
import com.mredust.oj.codesandbox.model.dto.ExecuteCodeResponse;

/**
 * 执行代码接口
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface CodeSandboxService {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
