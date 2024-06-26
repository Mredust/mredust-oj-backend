package com.mredust.oj.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.config.redis.RedisService;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.mapper.ProblemMapper;
import com.mredust.oj.model.dto.problem.ProblemAddRequest;
import com.mredust.oj.model.dto.problem.ProblemQueryRequest;
import com.mredust.oj.model.dto.problem.ProblemUpdateRequest;
import com.mredust.oj.model.dto.problem.TemplateCode;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.mredust.oj.constant.RedisConstant.PROBLEM_KEY;
import static com.mredust.oj.constant.RedisConstant.TIMEOUT_TTL;


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
    
    @Resource
    private RedisService redisService;
    
    /**
     * 新增题目
     *
     * @param problemAddRequest
     * @return 新增的题目id
     */
    @Override
    public long addProblem(ProblemAddRequest problemAddRequest) {
        Problem problem = new Problem();
        BeanUtils.copyProperties(problemAddRequest, problem);
        Long userId = userService.getLoginUser().getId();
        problem.setUserId(userId);
        List<TemplateCode> templateCode = problemAddRequest.getTemplateCode();
        List<String> tags = problemAddRequest.getTags();
        List<String> testCase = problemAddRequest.getTestCase();
        List<String> testAnswer = problemAddRequest.getTestAnswer();
        fieldFill(problem, templateCode, tags, testCase, testAnswer);
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
        List<TemplateCode> templateCode = problemUpdateRequest.getTemplateCode();
        List<String> tags = problemUpdateRequest.getTags();
        List<String> testCase = problemUpdateRequest.getTestCase();
        List<String> testAnswer = problemUpdateRequest.getTestAnswer();
        fieldFill(problem, templateCode, tags, testCase, testAnswer);
        boolean result = this.updateById(problem);
        if (!result) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }
        String problemKey = PROBLEM_KEY + problem.getId();
        String jsonStr = JSONUtil.toJsonStr(problem);
        long timeout = TIMEOUT_TTL + RandomUtil.randomLong(1, 3);
        redisService.setCacheObject(problemKey, jsonStr, timeout, TimeUnit.DAYS);
        return true;
    }
    
    private void fieldFill(Problem problem, List<TemplateCode> templateCode, List<String> tags, List<String> testCase, List<String> testAnswer) {
        if (templateCode != null) {
            problem.setTemplateCode(JSONUtil.toJsonStr(templateCode));
        }
        if (tags != null) {
            problem.setTags(JSONUtil.toJsonStr(tags));
        }
        if (testCase != null) {
            problem.setTestCase(JSONUtil.toJsonStr(testCase));
        }
        if (testAnswer != null) {
            problem.setTestAnswer(JSONUtil.toJsonStr(testAnswer));
        }
    }
    
    
    @Override
    public Page<Problem> getProblemListByPage(ProblemQueryRequest problemQueryRequest) {
        long pageNum = problemQueryRequest.getPageNum();
        long pageSize = problemQueryRequest.getPageSize();
        Page<Problem> page = new Page<>(pageNum, pageSize);
        return conditionQueryWrapper(problemQueryRequest).page(page);
    }
    
    
    private LambdaQueryChainWrapper<Problem> conditionQueryWrapper(ProblemQueryRequest problemQueryRequest) {
        Long id = problemQueryRequest.getId();
        String keyword = problemQueryRequest.getKeyword();
        List<String> tags = problemQueryRequest.getTags();
        Integer difficulty = problemQueryRequest.getDifficulty();
        String sortField = problemQueryRequest.getSortField();
        String sortOrder = problemQueryRequest.getSortOrder();
        return Db.lambdaQuery(Problem.class)
                .eq(id != null, Problem::getId, id)
                .eq(difficulty != null, Problem::getDifficulty, difficulty)
                .like(StringUtils.isNotBlank(keyword), Problem::getTitle, keyword).or()
                .like(StringUtils.isNotBlank(keyword), Problem::getContent, keyword)
                .like(tags != null && !tags.isEmpty(), Problem::getTags, tags)
                .last(StringUtils.isNotBlank(sortField), "order by " + sortField + " " + sortOrder);
    }
    
    // todo：优化条件查询
    @Override
    public Page<ProblemVO> getProblemListVoByPage(ProblemQueryRequest problemQueryRequest) {
        Integer status = problemQueryRequest.getStatus();
        long pageNum = problemQueryRequest.getPageNum();
        long pageSize = problemQueryRequest.getPageSize();
        Page<ProblemVO> problemVOPage = new Page<>(pageNum, pageSize);
        Object userId = StpUtil.getLoginIdDefaultNull();
        Set<Long> submitIds = Collections.emptySet();
        if (userId != null && status != null) {
            submitIds = Db.lambdaQuery(ProblemSubmit.class)
                    .eq(ProblemSubmit::getUserId, userId)
                    .eq(ProblemSubmit::getStatus, status)
                    .list()
                    .stream().map(ProblemSubmit::getProblemId)
                    .collect(Collectors.toSet());
        }
      
        Page<Problem> problemPage = conditionQueryWrapper(problemQueryRequest)
                .in(!submitIds.isEmpty(), Problem::getId, submitIds)
                .page(new Page<>(pageNum, pageSize));
        List<ProblemVO> problemVOList = problemPage.getRecords().stream()
                .map(this::objToVo)
                .collect(Collectors.toList());
        problemVOPage.setRecords(problemVOList);
        problemVOPage.setTotal(problemPage.getTotal());
        return problemVOPage;
    }
    
    /**
     * 对象转包装类
     *
     * @param problem
     * @return
     */
    @Override
    public ProblemVO objToVo(Problem problem) {
        if (problem == null) {
            return null;
        }
        Object userId = StpUtil.getLoginIdDefaultNull();
        ProblemVO problemVo = new ProblemVO();
        BeanUtils.copyProperties(problem, problemVo);
        problemVo.setTags(JSONUtil.toList(problem.getTags(), String.class));
        problemVo.setTestCase(JSONUtil.toList(problem.getTestCase(), String.class));
        problemVo.setTestAnswer(JSONUtil.toList(problem.getTestAnswer(), String.class));
        problemVo.setTemplateCode(JSONUtil.toList(problem.getTemplateCode(), TemplateCode.class));
        if (userId != null) {
            ProblemSubmit problemSubmit = problemSubmitService.getOne(new QueryWrapper<ProblemSubmit>()
                    .select("max(status) as status").lambda()
                    .eq(ProblemSubmit::getProblemId, problem.getId())
                    .eq(ProblemSubmit::getUserId, userId));
            Integer problemSubmitStatus = problemSubmit.getStatus();
            if (problemSubmitStatus != null) {
                String text = ProblemSubmitStatusEnum.getProblemSubmitTextByCode(problemSubmitStatus);
                problemVo.setStatus(text);
            } else {
                problemVo.setStatus(ProblemSubmitStatusEnum.WAITING.getText());
            }
            return problemVo;
        }
        problemVo.setStatus(ProblemSubmitStatusEnum.WAITING.getText());
        return problemVo;
    }
    
    
}




