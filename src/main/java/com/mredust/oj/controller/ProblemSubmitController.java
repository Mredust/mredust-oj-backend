package com.mredust.oj.controller;


import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.dto.problemsubmit.ProblemSubmitAddRequest;
import com.mredust.oj.model.entity.ProblemSubmit;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.service.ProblemSubmitService;
import com.mredust.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param problemSubmitAddRequest
     * @return 提交记录的 id
     */
    @PostMapping
    public BaseResponse<ProblemSubmit> problemSubmit(@RequestBody @NotNull @Valid ProblemSubmitAddRequest problemSubmitAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        ProblemSubmit submitResult = problemSubmitService.problemSubmit(problemSubmitAddRequest, loginUser);
        return Result.success(submitResult);
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
    // /**
    //  * 获取某次历史提交的详细信息
    //  *
    //  * @param id
    //  * @return
    //  */
    // @GetMapping("/{id}")
    // public BaseResponse<ProblemSubmitVo> getProblemSubmitVoById(@PathVariable("id") Long id) {
    //     UserInfoVo currentUser = UserUtils.getCurrentUser(userService.getCurrentUser());
    //     if (currentUser == null) {
    //         throw new BusinessException(AppHttpCodeEnum.NOT_LOGIN);
    //     }
    //     ProblemSubmit submit = problemSubmitService.getById(id);
    //     // 返回脱敏信息
    //     return Result.success(ProblemSubmitVo.objToVo(submit));
    // }
}
