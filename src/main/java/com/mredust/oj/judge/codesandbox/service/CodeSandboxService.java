package com.mredust.oj.judge.codesandbox.service;


import com.mredust.oj.judge.codesandbox.model.dto.ExecuteRequest;
import com.mredust.oj.judge.codesandbox.model.dto.ExecuteResponse;

/**
 * 执行代码接口
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface CodeSandboxService {
    ExecuteResponse executeCode(ExecuteRequest executeCodeRequest);
}
