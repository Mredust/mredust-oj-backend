package com.mredust.oj.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.google.gson.Gson;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.mapper.ProblemMapper;
import com.mredust.oj.model.dto.problem.*;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;
import com.mredust.oj.model.vo.ProblemVO;
import com.mredust.oj.service.ProblemService;
import com.mredust.oj.service.ProblemSubmitService;
import com.mredust.oj.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mredust
 * @description 针对表【problem(题库表)】的数据库操作Service实现
 * @createDate 2024-06-06 13:27:50
 */
@Service
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem>
        implements ProblemService {
    @Resource
    private UserService userService;
    
    @Resource
    private ProblemSubmitService problemSubmitService;
    
    private static final Gson GSON = new Gson();
    
    /**
     * 新增题目
     *
     * @param problemAddRequest
     * @return 新增的题目id
     */
    @Override
    public long addProblem(ProblemAddRequest problemAddRequest, long userId) {
        Problem problem = new Problem();
        BeanUtils.copyProperties(problemAddRequest, problem);
        problem.setUserId(userId);
        problem.setTags(GSON.toJson(problemAddRequest.getTags()));
        problem.setJudgeCase(GSON.toJson(problemAddRequest.getJudgeCase()));
        problem.setJudgeConfig(GSON.toJson(problemAddRequest.getJudgeConfig()));
        boolean result = this.save(problem);
        if (!result) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }
        return problem.getId();
    }
    
    /**
     * 更新题目
     *
     * @param problemUpdateRequest 更新题目请求
     * @return 是否更新成功
     */
    @Override
    public boolean updateProblem(ProblemUpdateRequest problemUpdateRequest) {
        Problem problem = new Problem();
        BeanUtils.copyProperties(problemUpdateRequest, problem);
        
        problem.setTags(GSON.toJson(problemUpdateRequest.getTags()));
        problem.setJudgeCase(GSON.toJson(problemUpdateRequest.getJudgeCase()));
        problem.setJudgeConfig(GSON.toJson(problemUpdateRequest.getJudgeConfig()));
        boolean result = this.updateById(problem);
        if (!result) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }
        return true;
    }
    
    
    /**
     * 分页获取题目列表
     *
     * @param problemQueryRequest 查询条件
     * @return 题目分页对象
     */
    @Override
    public Page<ProblemVO> getProblemListByPage(ProblemQueryRequest problemQueryRequest, Long userId) {
        String keyword = problemQueryRequest.getKeyword();
        List<String> tags = problemQueryRequest.getTags();
        Integer status = problemQueryRequest.getStatus();
        Integer difficulty = problemQueryRequest.getDifficulty();
        long pageNum = problemQueryRequest.getPageNum();
        long pageSize = problemQueryRequest.getPageSize();
        String sortField = problemQueryRequest.getSortField();
        String sortOrder = problemQueryRequest.getSortOrder();
        Page<Problem> problemPage = new Page<>(pageNum, pageSize);
        // 用户提交的题库
        List<Long> submitIds = Db.lambdaQuery(ProblemSubmit.class)
                .eq(ProblemSubmit::getUserId, userId)
                .eq(status != null, ProblemSubmit::getStatus, status)
                .list()
                .stream().map(ProblemSubmit::getProblemId)
                .collect(Collectors.toList());
        // todo 标签查询
        // 获取全部题库
        Page<Problem> problemPageResult = Db.lambdaQuery(Problem.class)
                .eq(difficulty != null, Problem::getDifficulty, difficulty)
                .like(StringUtils.isNotBlank(keyword), Problem::getTitle, keyword).or()
                .like(StringUtils.isNotBlank(keyword), Problem::getContent, keyword)
                .last(StringUtils.isNotBlank(sortField), "order by " + sortField + " " + sortOrder)
                .in(!submitIds.isEmpty(), Problem::getId, submitIds)
                .page(problemPage);
        
        List<ProblemVO> records = problemPageResult.getRecords().stream()
                .map(problem -> objToVo(problem, userId))
                .collect(Collectors.toList());
        Page<ProblemVO> page = new Page<>(pageNum, pageSize, problemPageResult.getTotal());
        page.setRecords(records);
        return page;
    }
    
    /**
     * 对象转包装类
     *
     * @param problem
     * @return
     */
    @Override
    public ProblemVO objToVo(Problem problem, Long userId) {
        if (problem == null) {
            return null;
        }
        ProblemVO problemVo = new ProblemVO();
        BeanUtils.copyProperties(problem, problemVo);
        problemVo.setTags(JSONUtil.toList(problem.getTags(), String.class));
        problemVo.setJudgeConfig(JSONUtil.toBean(problem.getJudgeConfig(), JudgeConfig.class));
        problemVo.setJudgeCase(JSONUtil.toList(problem.getJudgeCase(), JudgeCase.class));
        
        ProblemSubmit problemSubmit = problemSubmitService.getOne(new QueryWrapper<ProblemSubmit>()
                .select("max(status) as status").lambda()
                .eq(ProblemSubmit::getProblemId, problem.getId())
                .eq(ProblemSubmit::getUserId, userId));
        if (problemSubmit == null) {
            problemVo.setStatus(ProblemSubmitStatusEnum.WAITING.getText());
            return problemVo;
        }
        Integer problemSubmitStatus = problemSubmit.getStatus();
        String text = ProblemSubmitStatusEnum.getProblemSubmitTextByCode(problemSubmitStatus);
        problemVo.setStatus(text);
        return problemVo;
    }
}




