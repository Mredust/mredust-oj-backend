package com.mredust.oj.judge.codesandbox;

import com.mredust.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.mredust.oj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 执行代码接口
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface CodeSandbox {
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
