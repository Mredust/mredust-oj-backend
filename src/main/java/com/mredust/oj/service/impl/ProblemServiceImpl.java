package com.mredust.oj.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.google.gson.Gson;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.mapper.ProblemMapper;
import com.mredust.oj.model.dto.problem.ProblemAddRequest;
import com.mredust.oj.model.dto.problem.ProblemQueryRequest;
import com.mredust.oj.model.dto.problem.ProblemUpdateRequest;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.vo.ProblemVO;
import com.mredust.oj.service.ProblemService;
import com.mredust.oj.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        Problem oldProblem = this.getById(problem.getId());
        if (oldProblem == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
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
    public Page<Problem> getProblemListByPage(ProblemQueryRequest problemQueryRequest) {
        String title = problemQueryRequest.getTitle();
        String content = problemQueryRequest.getContent();
        List<String> tagList = problemQueryRequest.getTags();
        Long userId = problemQueryRequest.getUserId();
        return Db.lambdaQuery(Problem.class)
                .like(StringUtils.isNotBlank(title), Problem::getTitle, title)
                .like(StringUtils.isNotBlank(content), Problem::getContent, content)
                .like(CollUtil.isNotEmpty(tagList), Problem::getTags, JSONUtil.toJsonStr(tagList))
                .eq(ObjectUtils.isNotEmpty(userId), Problem::getUserId, userId)
                .page(new Page<>(problemQueryRequest.getPageNum(), problemQueryRequest.getPageSize()));
    }
    

}




