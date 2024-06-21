package com.mredust.oj.codesandbox.service;


import com.mredust.oj.codesandbox.model.dto.ExecuteRequest;
import com.mredust.oj.codesandbox.model.dto.ExecuteResponse;

/**
 * 执行代码接口
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface CodeSandboxService {
    ExecuteResponse executeCode(ExecuteRequest executeCodeRequest);
}
