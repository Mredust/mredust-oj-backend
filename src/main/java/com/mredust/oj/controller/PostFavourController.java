package com.mredust.oj.controller;

import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.dto.post.PostFavourAddRequest;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.service.PostFavourService;
import com.mredust.oj.service.PostService;
import com.mredust.oj.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏接口
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestController
@RequestMapping("/post-favour")
public class PostFavourController {
    
    @Resource
    private PostFavourService postFavourService;
    
    @Resource
    private PostService postService;
    
    @Resource
    private UserService userService;
    
    /**
     * 收藏 / 取消收藏
     *
     * @param postFavourAddRequest 帖子收藏请求
     * @param request              请求
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> postFavour(@RequestBody PostFavourAddRequest postFavourAddRequest, HttpServletRequest request) {
        if (postFavourAddRequest == null || postFavourAddRequest.getPostId() <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        int result = postFavourService.postFavour(postFavourAddRequest.getPostId(), loginUser.getId());
        return Result.success(result);
    }
}
