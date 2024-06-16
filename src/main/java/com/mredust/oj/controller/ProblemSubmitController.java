package com.mredust.oj.controller;


import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.problem.ProblemSubmitStatusEnum;
import com.mredust.oj.model.vo.ProblemSubmitVO;
import com.mredust.oj.service.ProblemSubmitService;
import com.mredust.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


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
    
    
    /**
     * 提交题目
     *
     * @param problemSubmitAddRequest 提交信息
     * @return 提交记录的 id
     */
    @PostMapping("/execute")
    public BaseResponse<ProblemSubmitVO> problemSubmit(@RequestBody @NotNull @Valid ProblemSubmitAddRequest problemSubmitAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        ProblemSubmitVO submitResult = problemSubmitService.problemSubmit(problemSubmitAddRequest, loginUser);
        return  Result.success(submitResult);
    }
    
    /**
     * 获取单次提交的详细信息
     *
     * @param id 提交记录的 id
     * @return 提交记录的详细信息
     */
    @GetMapping("/get")
    public BaseResponse<ProblemSubmitVO> getProblemSubmitVoById(@RequestParam("id") Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        ProblemSubmitVO problemSubmitVO = problemSubmitService.getProblemSubmitVoById(id);
        return Result.success(problemSubmitVO);
    }
    
    // /**
    //  * 分页获取题目提交历史
    //  *
    //  * @param problemSubmitQueryRequest
    //  * @return
    //  */
    // @PostMapping("/page/vo")
    // public BaseResponse<Page<ProblemSubmitVo>> listProblemSubmitVoByPage(@RequestBody ProblemSubmitQueryRequest problemSubmitQueryRequest) {
    //     UserInfoVo currentUser = UserUtils.getCurrentUser(userService.getCurrentUser());
    //     if (currentUser == null) {
    //         throw new BusinessException(AppHttpCodeEnum.NOT_LOGIN);
    //     }
    //     Page<ProblemSubmitVo> page = problemSubmitService.listProblemSubmitVoByPage(problemSubmitQueryRequest, currentUser.getUid());
    //     // 返回脱敏信息
    //     return Result.success(page);
    // }
    //
    
}
