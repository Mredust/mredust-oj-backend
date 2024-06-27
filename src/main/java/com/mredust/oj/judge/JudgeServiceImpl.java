package com.mredust.oj.judge;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.judge.codesandbox.model.dto.ExecuteRequest;
import com.mredust.oj.judge.codesandbox.model.dto.ExecuteResponse;
import com.mredust.oj.judge.codesandbox.model.enums.ExecuteResponseEnum;
import com.mredust.oj.judge.codesandbox.service.CodeSandboxService;
import com.mredust.oj.mapper.ProblemMapper;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.enums.problem.JudgeInfoEnum;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;
import com.mredust.oj.service.ProblemService;
import com.mredust.oj.service.ProblemSubmitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;


/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Service
public class JudgeServiceImpl implements JudgeService {
    @Resource
    private ProblemSubmitService problemSubmitService;
    @Resource
    private ProblemService problemService;
    @Resource
    private CodeSandboxService codeSandboxService;
    @Resource
    private ProblemMapper problemMapper;
    
    @Override
    public ProblemSubmit handleJudge(long problemSubmitId) {
        ProblemSubmit problemSubmit = problemSubmitService.getById(problemSubmitId);
        if (problemSubmit == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "提交信息不存在");
        }
        Long problemId = problemSubmit.getProblemId();
        Problem problem = problemService.getById(problemId);
        if (problem == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "题目不存在");
        }
        if (!problemSubmit.getStatus().equals(ProblemSubmitStatusEnum.WAITING.getCode())) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "题目正在判题中");
        }
        ProblemSubmit updateSubmitInfo = new ProblemSubmit();
        updateSubmitInfo.setId(problemSubmitId);
        updateSubmitInfo.setStatus(ProblemSubmitStatusEnum.RUNNING.getCode());
        updateSubmitInfo.setMessage(ProblemSubmitStatusEnum.RUNNING.getStatus());
        updateStatus(updateSubmitInfo);
        
        // 调用沙箱，获取到执行结果
        String language = problemSubmit.getLanguage();
        String code = problemSubmit.getCode();
        String testCase = problem.getTestCase();
        List<String> testCaseList = JSONUtil.toList(testCase, String.class);
        ExecuteRequest executeCodeRequest = ExecuteRequest.builder()
                .code(code)
                .language(language)
                .testCaseList(testCaseList)
                .build();
        ExecuteResponse response = codeSandboxService.executeCode(executeCodeRequest);
        Integer statusCode = response.getCode();
        String msg = response.getMsg();
        List<String> stdout = response.getStdout();
        String stderr = response.getStderr();
        Long runTime = Optional.ofNullable(response.getRunTime()).orElse(0L);
        Long runMemory = Optional.ofNullable(response.getRunMemory()).orElse(0L);
        
        
        updateSubmitInfo.setRunTime(runTime);
        updateSubmitInfo.setRunMemory(runMemory);
        // todo 优化获取判题堆栈
        updateSubmitInfo.setRunStack(0L);
        updateSubmitInfo.setMessage(msg);
        String testCaseAnswer = problem.getTestAnswer();
        List<String> testCaseAnswerList = JSONUtil.toList(testCaseAnswer, String.class);
        Long problemRunTime = problem.getRunTime();
        Long problemRunMemory = problem.getRunMemory();
        
        
        if (runTime > problemRunTime) {
            handleSubmissionStatus(updateSubmitInfo, ProblemSubmitStatusEnum.FAILED, JudgeInfoEnum.TIME_LIMIT_EXCEEDED.getText());
        } else if (runMemory > problemRunMemory) {
            handleSubmissionStatus(updateSubmitInfo, ProblemSubmitStatusEnum.FAILED, JudgeInfoEnum.MEMORY_LIMIT_EXCEEDED.getText());
        } else {
            if (statusCode.equals(ExecuteResponseEnum.RUN_SUCCESS.getCode())) {
                boolean allTestCasesPassed = true;
                if (testCaseAnswerList.size() != stdout.size()) {
                    allTestCasesPassed = false;
                } else {
                    for (int i = 0; i < testCaseAnswerList.size(); i++) {
                        String s1 = testCaseAnswerList.get(i);
                        String s2 = stdout.get(i);
                        if (!s1.equals(s2)) {
                            allTestCasesPassed = false;
                            break;
                        }
                    }
                }
                if (allTestCasesPassed) {
                    handleSubmissionStatus(updateSubmitInfo, ProblemSubmitStatusEnum.SUCCEED, JudgeInfoEnum.ACCEPTED.getText());
                    problemMapper.update(null, new UpdateWrapper<Problem>().setSql("accepted_num = accepted_num + 1").eq("id", problem.getId()));
                } else {
                    handleSubmissionStatus(updateSubmitInfo, ProblemSubmitStatusEnum.FAILED, JudgeInfoEnum.WRONG_ANSWER.getText());
                }
            } else {
                handleSubmissionStatus(updateSubmitInfo, ProblemSubmitStatusEnum.FAILED, msg, stderr);
            }
        }
        return updateSubmitInfo;
    }
    
    
    private void updateStatus(ProblemSubmit problemSubmit) {
        boolean flag = problemSubmitService.updateById(problemSubmit);
        if (!flag) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "题目状态更新失败");
        }
    }
    
    private void handleSubmissionStatus(ProblemSubmit problemSubmit, ProblemSubmitStatusEnum status, String message, String... errorMessage) {
        problemSubmit.setStatus(status.getCode());
        problemSubmit.setMessage(message);
        if (errorMessage.length > 0) {
            problemSubmit.setErrorMessage(errorMessage[0]);
        }
        updateStatus(problemSubmit);
    }
}
