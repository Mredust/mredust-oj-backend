package com.mredust.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mredust.oj.annotation.AuthCheck;
import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.DeleteRequest;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.dto.problem.ProblemAddRequest;
import com.mredust.oj.model.dto.problem.ProblemQueryRequest;
import com.mredust.oj.model.dto.problem.ProblemUpdateRequest;
import com.mredust.oj.model.entity.Problem;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.vo.ProblemVO;
import com.mredust.oj.service.ProblemService;
import com.mredust.oj.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

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
    
    // region 增删改查
    
    /**
     * 新增题目
     *
     * @param problemAddRequest 新增题目请求
     * @param request           请求
     * @return 新增的题目id
     */
    @PostMapping("/add")
    @AuthCheck(role = ADMIN_ROLE)
    public BaseResponse<Long> addProblem(@RequestBody @Valid ProblemAddRequest problemAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long problemId = problemService.addProblem(problemAddRequest, loginUser.getId());
        return Result.success(problemId);
    }
    
    /**
     * 删除题目
     *
     * @param deleteRequest 删除请求
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    @AuthCheck(role = ADMIN_ROLE)
    public BaseResponse<Boolean> deleteProblem(@RequestBody @Valid DeleteRequest deleteRequest) {
        Long problemId = deleteRequest.getId();
        Problem problem = problemService.getById(problemId);
        if (problem == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        boolean result = problemService.removeById(problemId);
        return Result.success(result);
    }
    
    /**
     * 更新题目
     *
     * @param problemUpdateRequest
     * @return
     */
    @PutMapping("/update")
    @AuthCheck(role = ADMIN_ROLE)
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
    public BaseResponse<ProblemVO> getProblemById(@RequestParam("id") @NotNull Long id, HttpServletRequest request) {
        Problem problem = problemService.getById(id);
        if (problem == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        User loginUser = userService.getLoginUser(request);
        ProblemVO problemVO = problemService.objToVo(problem, loginUser.getId());
        return Result.success(problemVO);
    }
    
    /**
     * 分页获取题目列表
     *
     * @param problemQueryRequest 查询条件
     * @return 题目分页对象
     */
    @PostMapping("/list")
    public BaseResponse<Page<ProblemVO>> getProblemListByPage(@RequestBody ProblemQueryRequest problemQueryRequest, HttpServletRequest request) {
        if (problemQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        User loginUser = userService.getLoginUser(request);
        Page<ProblemVO> list = problemService.getProblemListByPage(problemQueryRequest, loginUser.getId());
        return Result.success(list);
    }
    
    
    // endregion
}
