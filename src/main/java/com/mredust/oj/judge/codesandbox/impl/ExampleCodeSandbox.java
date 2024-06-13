package com.mredust.oj.judge.codesandbox.impl;

import com.mredust.oj.judge.codesandbox.CodeSandbox;
import com.mredust.oj.judge.codesandbox.model.ExecuteCodeRequest;
import com.mredust.oj.judge.codesandbox.model.ExecuteCodeResponse;
import com.mredust.oj.judge.codesandbox.model.JudgeInfo;
import com.mredust.oj.model.enums.problem.JudgeInfoEnum;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;

import java.util.List;

/**
 * 实列代码沙箱
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试执行成功");
        executeCodeResponse.setStatus(ProblemSubmitStatusEnum.SUCCEED.getCode());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoEnum.ACCEPTED.getText());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }
}
