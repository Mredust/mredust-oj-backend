package com.mredust.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.judge.codesandbox.service.CodeSandboxService;
import com.mredust.oj.mapper.ProblemMapper;
import com.mredust.oj.mapper.ProblemSubmitMapper;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitQueryRequest;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;
import com.mredust.oj.model.vo.ProblemSubmitVO;
import com.mredust.oj.rabbitmq.MQMessageProducer;
import com.mredust.oj.service.ProblemSubmitService;
import com.mredust.oj.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static com.mredust.oj.constant.MqConstant.JUDGE_EXCHANGE;
import static com.mredust.oj.constant.MqConstant.JUDGE_QUEUE_ROUTING_KEY;


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
    private UserService userService;
    @Resource
    private CodeSandboxService codeSandboxService;
    
    @Resource
    private MQMessageProducer messageProducer;
    
    @Override
    public Long problemSubmit(ProblemSubmitAddRequest problemSubmitAddRequest, User loginUser) {
        String language = problemSubmitAddRequest.getLanguage();
        if (language == null) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "不支持的编程语言");
        }
        long problemId = problemSubmitAddRequest.getProblemId();
        Problem problem = problemMapper.selectById(problemId);
        if (problem == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        Long userId = loginUser.getId();
        ProblemSubmit submit = lambdaQuery().eq(ProblemSubmit::getUserId, userId)
                .and(wrapper -> wrapper.eq(ProblemSubmit::getStatus, ProblemSubmitStatusEnum.WAITING).or()
                        .eq(ProblemSubmit::getStatus, ProblemSubmitStatusEnum.RUNNING)).one();
        if (submit != null) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR, "提交过于频繁！");
        }
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
        // 将problem的提交数+1
        problemMapper.update(null, new UpdateWrapper<Problem>().setSql("submit_num = submit_num + 1").eq("id", problem.getId()));
        // 执行判题服务
        Long problemSubmitId = problemSubmit.getId();
        messageProducer.sendMessage(JUDGE_EXCHANGE, JUDGE_QUEUE_ROUTING_KEY, problemSubmitId.toString());
        return problemSubmitId;
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
    
    
    @Override
    public Page<ProblemSubmit> getProblemSubmitListByPage(ProblemSubmitQueryRequest problemSubmitQueryRequest) {
        String language = problemSubmitQueryRequest.getLanguage();
        Integer status = problemSubmitQueryRequest.getStatus();
        Long problemId = problemSubmitQueryRequest.getProblemId();
        String message = problemSubmitQueryRequest.getMessage();
        long pageNum = problemSubmitQueryRequest.getPageNum();
        long pageSize = problemSubmitQueryRequest.getPageSize();
        String sortField = problemSubmitQueryRequest.getSortField();
        String sortOrder = problemSubmitQueryRequest.getSortOrder();
        Page<ProblemSubmit> page = new Page<>(pageNum, pageSize);
        Long userId = userService.getLoginUser().getId();
        return Db.lambdaQuery(ProblemSubmit.class)
                .like(StringUtils.isNotBlank(language), ProblemSubmit::getLanguage, language)
                .like(StringUtils.isNotBlank(message), ProblemSubmit::getMessage, message)
                .eq(status != null, ProblemSubmit::getStatus, status)
                .eq(problemId != null, ProblemSubmit::getProblemId, problemId)
                .eq(userId != null, ProblemSubmit::getProblemId, userId)
                .last(StringUtils.isNotBlank(sortField), "order by " + sortField + " " + sortOrder)
                .page(page);
    }
}




