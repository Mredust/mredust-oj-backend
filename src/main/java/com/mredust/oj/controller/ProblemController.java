package com.mredust.oj.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mredust.oj.judge.codesandbox.model.enums.LanguageEnum;
import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.DeleteRequest;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import com.mredust.oj.config.redis.RedisService;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.dto.problem.ProblemAddRequest;
import com.mredust.oj.model.dto.problem.ProblemQueryRequest;
import com.mredust.oj.model.dto.problem.ProblemUpdateRequest;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.enums.problem.JudgeInfoEnum;
import com.mredust.oj.model.vo.ProblemVO;
import com.mredust.oj.service.ProblemService;
import com.mredust.oj.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mredust.oj.constant.RedisConstant.*;
import static com.mredust.oj.constant.UserConstant.ADMIN_ROLE;

/**
 * 题目接口
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestController
@Validated
@RequestMapping("/problem")
public class ProblemController {
    
    @Resource
    private ProblemService problemService;
    @Resource
    private UserService userService;
    @Resource
    private RedisService redisService;
    
    // region 增删改查
    
    /**
     * 新增题目
     *
     * @param problemAddRequest 新增题目请求
     * @return 新增的题目id
     */
    @PostMapping("/add")
    @SaCheckRole(ADMIN_ROLE)
    public BaseResponse<Long> addProblem(@RequestBody @Valid ProblemAddRequest problemAddRequest) {
        long problemId = problemService.addProblem(problemAddRequest);
        return Result.success(problemId);
    }
    
    /**
     * 删除题目
     *
     * @param deleteRequest 删除请求
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    @SaCheckRole(ADMIN_ROLE)
    public BaseResponse<Boolean> deleteProblem(@RequestBody @Valid DeleteRequest deleteRequest) {
        Long problemId = deleteRequest.getId();
        Problem problem = problemService.getById(problemId);
        if (problem == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        boolean result = problemService.removeById(problemId);
        redisService.deleteCacheObject(PROBLEM_KEY + problemId);
        return Result.success(result);
    }
    
    /**
     * 更新题目
     *
     * @param problemUpdateRequest
     * @return
     */
    @PutMapping("/update")
    @SaCheckRole(ADMIN_ROLE)
    public BaseResponse<Boolean> updateProblem(@RequestBody @Valid ProblemUpdateRequest problemUpdateRequest) {
        Problem problem = problemService.getById(problemUpdateRequest.getId());
        if (problem == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        boolean result = problemService.updateProblem(problemUpdateRequest);
        return Result.success(result);
    }
    
    /**
     * 根据 id 获取题目
     *
     * @param id 题目id
     * @return 题目信息
     */
    @GetMapping("/get")
    public BaseResponse<ProblemVO> getProblemById(@RequestParam("id") @NotNull Long id) {
        String problemKey = PROBLEM_KEY + id;
        String cacheProblem = redisService.getCacheObject(problemKey);
        if (StringUtils.isNotBlank(cacheProblem)) {
            Problem problem = JSONUtil.toBean(cacheProblem, Problem.class);
            ProblemVO problemVO = problemService.objToVo(problem);
            return Result.success(problemVO);
        }
        Problem problem = problemService.getById(id);
        long timeout = TIMEOUT_TTL + RandomUtil.randomLong(1, 3);
        if (problem == null) {
            redisService.setCacheObject(problemKey, StringUtils.EMPTY, timeout, TimeUnit.HOURS);
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        String jsonStr = JSONUtil.toJsonStr(problem);
        redisService.setCacheObject(problemKey, jsonStr, timeout, TimeUnit.HOURS);
        ProblemVO problemVO = problemService.objToVo(problem);
        return Result.success(problemVO);
    }
    
    /**
     * 分页获取题目列表(管理员)
     *
     * @param problemQueryRequest 查询条件
     * @return 题目分页对象
     */
    @PostMapping("/list")
    @SaCheckRole(ADMIN_ROLE)
    public BaseResponse<Page<ProblemVO>> getProblemListByPage(@RequestBody ProblemQueryRequest problemQueryRequest) {
        if (problemQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        Page<ProblemVO> problemPage = problemService.getProblemListByPage(problemQueryRequest);
        return Result.success(problemPage);
    }
    
    
    /**
     * 分页获取题目列表(用户)
     *
     * @param problemQueryRequest 查询条件
     * @return 题目分页对象
     */
    @PostMapping("/list/vo")
    public BaseResponse<Page<ProblemVO>> getProblemListVoByPage(@RequestBody ProblemQueryRequest problemQueryRequest) {
        if (problemQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        Page<ProblemVO> problemPage = problemService.getProblemListVoByPage(problemQueryRequest);
        return Result.success(problemPage);
    }
    // endregion
    
    /**
     * 获取语言列表
     *
     * @return 语言列表
     */
    @GetMapping("/language")
    public BaseResponse<List<String>> getLanguageList() {
        String languageList = redisService.getCacheObject(LANGUAGE_LIST_KEY);
        if (languageList != null) {
            List<String> list = JSONUtil.toBean(languageList, new TypeReference<List<String>>() {
            }, true);
            return Result.success(list);
        }
        ArrayList<String> list = new ArrayList<>();
        for (LanguageEnum value : LanguageEnum.values()) {
            list.add(value.getValue());
        }
        long timeout = TIMEOUT_TTL + RandomUtil.randomLong(1, 10);
        redisService.setCacheObject(LANGUAGE_LIST_KEY, JSONUtil.toJsonStr(list), timeout, TimeUnit.DAYS);
        return Result.success(list);
    }
    
  
}
