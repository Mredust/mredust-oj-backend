package com.mredust.oj.controller;


import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import com.mredust.oj.config.redis.RedisService;
import com.mredust.oj.config.redisson.RedissonService;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitQueryRequest;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.problem.JudgeInfoEnum;
import com.mredust.oj.model.vo.ProblemSubmitVO;
import com.mredust.oj.service.ProblemSubmitService;
import com.mredust.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mredust.oj.constant.RedisConstant.*;


/**
 * @author Mredust
 */
@RestController
@RequestMapping("/problem_submit")
@Slf4j
@Validated
public class ProblemSubmitController {
    @Resource
    private ProblemSubmitService problemSubmitService;
    @Resource
    private UserService userService;
    @Resource
    private RedisService redisService;
    
    @Resource
    private RedissonService redissonService;
    
    /**
     * 提交题目
     *
     * @param problemSubmitAddRequest 提交信息
     * @return 提交记录的 id
     */
    @PostMapping("/execute")
    public BaseResponse<Long> problemSubmit(@RequestBody @NotNull @Valid ProblemSubmitAddRequest problemSubmitAddRequest) {
        User loginUser = userService.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        Long problemSubmitId = problemSubmitService.problemSubmit(problemSubmitAddRequest, loginUser);
        return Result.success(problemSubmitId);
    }
    
    /**
     * 获取单次提交的详细信息
     *
     * @param id 提交记录的 id
     * @return 提交记录的详细信息
     */
    @GetMapping("/get")
    public BaseResponse<ProblemSubmitVO> getProblemSubmitVoById(@RequestParam("id") Long id) {
        User loginUser = userService.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        String key = LIMIT_KEY_PREFIX + loginUser.getId();
        boolean flag = redissonService.handleRateLimit(key);
        if (!flag) {
            throw new BusinessException(ResponseCode.SUBMIT_TOO_FAST);
        }
        ProblemSubmitVO problemSubmitVO = problemSubmitService.getProblemSubmitVoById(id);
        return Result.success(problemSubmitVO);
    }
    
    /**
     * 分页获取题目提交历史
     *
     * @param problemSubmitQueryRequest
     * @return
     */
    @PostMapping("/list")
    public BaseResponse<Page<ProblemSubmit>> getProblemSubmitListByPage(@RequestBody ProblemSubmitQueryRequest problemSubmitQueryRequest) {
        if (problemSubmitQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        Page<ProblemSubmit> page = problemSubmitService.getProblemSubmitListByPage(problemSubmitQueryRequest);
        return Result.success(page);
    }
    
    /**
     * 获取提交状态列表
     *
     * @return
     */
    @GetMapping("/status")
    public BaseResponse<List<String>> getSubmitStatusList() {
        String statusList = redisService.getCacheObject(STATUS_LIST_KEY);
        if (statusList != null) {
            List<String> list = JSONUtil.toBean(statusList, new TypeReference<List<String>>() {
            }, true);
            return Result.success(list);
        }
        ArrayList<String> list = new ArrayList<>();
        for (JudgeInfoEnum value : JudgeInfoEnum.values()) {
            list.add(value.getText());
        }
        long timeout = TIMEOUT_TTL + RandomUtil.randomLong(1, 10);
        redisService.setCacheObject(STATUS_LIST_KEY, JSONUtil.toJsonStr(list), timeout, TimeUnit.DAYS);
        return Result.success(list);
    }
    
}
