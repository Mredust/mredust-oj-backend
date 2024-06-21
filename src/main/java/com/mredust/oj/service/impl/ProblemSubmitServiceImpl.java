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
import com.mredust.oj.model.dto.problem.JudgeCase;
import com.mredust.oj.model.dto.problem.JudgeConfig;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.problem.JudgeInfoEnum;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;
import com.mredust.oj.model.vo.JudgeInfo;
import com.mredust.oj.model.vo.ProblemSubmitVO;
import com.mredust.oj.service.ProblemSubmitService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


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
        boolean save = this.save(problemSubmit);
        if (!save) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "数据插入失败");
        }
        // 执行判题服务
        return handleJudge(problemSubmit, problem);
    }
    
    private ProblemSubmitVO handleJudge(ProblemSubmit problemSubmit, Problem problem) {
        problemSubmit.setStatus(ProblemSubmitStatusEnum.RUNNING.getCode());
        boolean flag = this.updateById(problemSubmit);
        if (!flag) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "题目状态更新失败");
        }
        // 调用沙箱，获取到执行结果
        String language = problemSubmit.getLanguage();
        String code = problemSubmit.getCode();
        // 获取输入用例
        List<JudgeCase> judgeCaseList = JSONUtil.toList(problem.getJudgeCase(), JudgeCase.class);
        // List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        List<String[]> inputList = judgeCaseList.stream()
                .map(JudgeCase::getInput)
                .map(input -> input.split(","))  // 根据具体分隔符进行拆分
                .collect(Collectors.toList());
        
        List<String> answerOutputList = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
        ExecuteRequest executeCodeRequest = ExecuteRequest.builder()
                .code(code)
                .language(language)
                .testCaseList(inputList)
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
        BeanUtil.copyProperties(problemSubmit, problemSubmitVO);
        JudgeConfig problemJudgeConfig = JSONUtil.toBean(problem.getJudgeConfig(), JudgeConfig.class);
        JudgeConfig judgeConfig = new JudgeConfig();
        judgeConfig.setTimeLimit(runTime);
        judgeConfig.setMemoryLimit(runMemory);
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setStatus(statusCode);
        // 执行成功
        if (statusCode.equals(ExecuteResponseEnum.RUN_SUCCESS.getCode())) {
            // 判题配置
            if (answerOutputList.size() == stdout.size()) {
                for (int i = 0; i < answerOutputList.size(); i++) {
                    if (runTime > problemJudgeConfig.getTimeLimit()) {
                        judgeInfo.setMessage(JudgeInfoEnum.TIME_LIMIT_EXCEEDED.getText());
                        judgeInfo.setJudgeConfig(judgeConfig);
                        problemSubmitVO.setJudgeInfo(judgeInfo);
                        return problemSubmitVO;
                    }
                    if (!answerOutputList.get(i).equals(stdout.get(i))) {
                        // 遇到了一个没通过的
                        problemSubmit.setStatus(ProblemSubmitStatusEnum.FAILED.getCode());
                        problemSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
                        judgeInfo.setMessage(ProblemSubmitStatusEnum.FAILED.getStatus());
                        problemSubmitVO.setJudgeInfo(judgeInfo);
                        updateById(problemSubmit);
                        return problemSubmitVO;
                    }
                }
            }
        } else if (statusCode.equals(ExecuteResponseEnum.RUNTIME_ERROR.getCode())) {
            judgeInfo.setMessage(stderr);
        } else if (statusCode.equals(ExecuteResponseEnum.COMPILE_ERROR.getCode())) {
            judgeInfo.setMessage(stderr);
        }
        
        // 5、修改数据库中的判题结果
        boolean isSuccess = ExecuteResponseEnum.RUN_SUCCESS.getCode().equals(statusCode);
        problemSubmit.setStatus(isSuccess ?
                ProblemSubmitStatusEnum.SUCCEED.getCode() :
                ProblemSubmitStatusEnum.FAILED.getCode());
        judgeInfo.setMessage(msg);
        judgeInfo.setJudgeConfig(judgeConfig);
        problemSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        flag = this.updateById(problemSubmit);
        if (!flag) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "题目状态更新失败");
        }
        // 6、修改题目的通过数
        if (isSuccess) {
            problemMapper.update(null, new UpdateWrapper<Problem>().setSql("accepted_num = accepted_num + 1").eq("id", problem.getId()));
        }
        problemSubmitVO.setStatus(isSuccess ?
                ProblemSubmitStatusEnum.SUCCEED.getCode() :
                ProblemSubmitStatusEnum.FAILED.getCode());
        problemSubmitVO.setJudgeInfo(judgeInfo);
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
        problemSubmitVO.setJudgeInfo(JSONUtil.toBean(problemSubmit.getJudgeInfo(), JudgeInfo.class));
        return problemSubmitVO;
    }
}




