package com.mredust.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.mapper.ProblemMapper;
import com.mredust.oj.mapper.ProblemSubmitMapper;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;
import com.mredust.oj.service.ProblemSubmitService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    
    @Override
    public ProblemSubmit problemSubmit(ProblemSubmitAddRequest problemSubmitAddRequest, User loginUser) {
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
        problemSubmit.setUserId(userId);
        problemSubmit.setProblemId(problemId);
        problemSubmit.setCode(problemSubmitAddRequest.getCode());
        problemSubmit.setLanguage(language);
        // 设置初始状态
        problemSubmit.setStatus(ProblemSubmitStatusEnum.WAITING.getCode());
        problemSubmit.setJudgeInfo("{}");
        boolean save = this.save(problemSubmit);
        if (!save) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "数据插入失败");
        }
        // 执行判题服务
        // ProblemSubmit submitResult = judgeService.doJudge(problemSubmit, currentUser.getAccessKey(), currentUser.getSecretKey());
        return null;
    }
}




