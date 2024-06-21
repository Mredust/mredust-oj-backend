package com.mredust.oj.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.mredust.oj.codesandbox.model.dto.ExecuteRequest;
import com.mredust.oj.codesandbox.model.dto.ExecuteResponse;
import com.mredust.oj.codesandbox.model.enums.ExecuteResponseEnum;
import com.mredust.oj.codesandbox.service.CodeSandboxService;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.mapper.ProblemMapper;
import com.mredust.oj.mapper.ProblemSubmitMapper;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.problem.JudgeInfoEnum;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;
import com.mredust.oj.model.vo.ProblemSubmitVO;
import com.mredust.oj.service.ProblemSubmitService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author Mredust
 * @description 针对表【problem_submit(题目提交表)】的数据库操作Service实现
 * @createDate 2024-06-06 13:27:55
 */
@Service
public class ProblemSubmitServiceImpl extends ServiceImpl<ProblemSubmitMapper, ProblemSubmit>
        implements ProblemSubmitService {
    
    @Resource
    private ProblemMapper problemMapper;
    
    @Resource
    private CodeSandboxService codeSandboxService;
    
    @Override
    public ProblemSubmitVO problemSubmit(ProblemSubmitAddRequest problemSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = problemSubmitAddRequest.getLanguage();
        if (language == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "不支持的编程语言");
        }
        long problemId = problemSubmitAddRequest.getProblemId();
        // 判断实体是否存在，根据类别获取实体
        Problem problem = problemMapper.selectById(problemId);
        if (problem == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        
        // 判断用户是否有正在等待或判题的题，如果有，提交判题失败
        Long userId = loginUser.getId();
        ProblemSubmit submit = lambdaQuery().eq(ProblemSubmit::getUserId, userId)
                .and(wrapper -> wrapper.eq(ProblemSubmit::getStatus, ProblemSubmitStatusEnum.WAITING).or()
                        .eq(ProblemSubmit::getStatus, ProblemSubmitStatusEnum.RUNNING)).one();
        if (submit != null) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "提交过于频繁！");
        }
        
        // 将problem的提交数+1
        problemMapper.update(null, new UpdateWrapper<Problem>().setSql("submit_num = submit_num + 1").eq("id", problem.getId()));
        
        // 是否已提交题目
        // 每个用户串行提交题目
        ProblemSubmit problemSubmit = new ProblemSubmit();
        problemSubmit.setProblemId(problemId);
        problemSubmit.setLanguage(language);
        problemSubmit.setCode(problemSubmitAddRequest.getCode());
        problemSubmit.setUserId(userId);
        // 设置初始状态
        problemSubmit.setStatus(ProblemSubmitStatusEnum.WAITING.getCode());
        problemSubmit.setMessage(ProblemSubmitStatusEnum.WAITING.getStatus());
        boolean flag = this.save(problemSubmit);
        if (!flag) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "题目状态插入失败");
        }
        // 执行判题服务
        return handleJudge(problemSubmit, problem);
    }
    
    private ProblemSubmitVO handleJudge(ProblemSubmit problemSubmit, Problem problem) {
        problemSubmit.setStatus(ProblemSubmitStatusEnum.RUNNING.getCode());
        problemSubmit.setMessage(ProblemSubmitStatusEnum.RUNNING.getStatus());
        updateStatus(problemSubmit);
        // 调用沙箱，获取到执行结果
        String language = problemSubmit.getLanguage();
        String code = problemSubmit.getCode();
        // 获取输入用例
        String testCase = problem.getTestCase();
        String testCaseAnswer = problem.getTestAnswer();
        List<String[]> testCaseList = JSONUtil.toList(testCase, String[].class);
        List<String> testCaseAnswerList = JSONUtil.toList(testCaseAnswer, String.class);
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
        Long runTime = response.getRunTime();
        Long runMemory = response.getRunMemory();
        
        // 根据沙箱的执行结果，设置题目的判题状态和信息
        ProblemSubmitVO problemSubmitVO = new ProblemSubmitVO();
        Long problemRunTime = problem.getRunTime();
        Long problemRunMemory = problem.getRunMemory();
        problemSubmitVO.setRunTime(0L);
        problemSubmitVO.setRunMemory(0L);
        problemSubmitVO.setRunStack(0L);
        if (runTime > problemRunTime) {
            handleSubmissionStatus(problemSubmit, ProblemSubmitStatusEnum.FAILED, JudgeInfoEnum.TIME_LIMIT_EXCEEDED.getText());
        } else if (runMemory > problemRunMemory) {
            handleSubmissionStatus(problemSubmit, ProblemSubmitStatusEnum.FAILED, JudgeInfoEnum.MEMORY_LIMIT_EXCEEDED.getText());
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
                    problemSubmit.setStatus(ProblemSubmitStatusEnum.SUCCEED.getCode());
                    problemSubmit.setMessage(msg);
                    problemSubmit.setRunTime(runTime);
                    problemSubmit.setRunMemory(runMemory);
                    updateStatus(problemSubmit);
                    problemMapper.update(null, new UpdateWrapper<Problem>().setSql("accepted_num = accepted_num + 1").eq("id", problem.getId()));
                } else {
                    handleSubmissionStatus(problemSubmit, ProblemSubmitStatusEnum.FAILED, JudgeInfoEnum.WRONG_ANSWER.getText());
                }
            } else {
                handleSubmissionStatus(problemSubmit, ProblemSubmitStatusEnum.FAILED, msg, stderr);
            }
        }
        BeanUtil.copyProperties(problemSubmit, problemSubmitVO);
        return problemSubmitVO;
    }
    
    @Override
    public ProblemSubmitVO getProblemSubmitVoById(Long id) {
        ProblemSubmit problemSubmit = Db.lambdaQuery(ProblemSubmit.class)
                .eq(id != null, ProblemSubmit::getId, id).one();
        // 返回脱敏信息
        if (problemSubmit == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        ProblemSubmitVO problemSubmitVO = new ProblemSubmitVO();
        BeanUtils.copyProperties(problemSubmit, problemSubmitVO);
        return problemSubmitVO;
    }
    
    
    private void handleSubmissionStatus(ProblemSubmit problemSubmit, ProblemSubmitStatusEnum status, String message, String... errorMessage) {
        problemSubmit.setStatus(status.getCode());
        problemSubmit.setMessage(message);
        problemSubmit.setErrorMessage(errorMessage[0]);
        updateStatus(problemSubmit);
    }
    
    private void updateStatus(ProblemSubmit problemSubmit) {
        boolean flag = this.updateById(problemSubmit);
        if (!flag) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "题目状态更新失败");
        }
    }
}




