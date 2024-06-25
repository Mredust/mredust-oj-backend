package com.mredust.oj.controller;

import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.dto.post.PostThumbAddRequest;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.service.PostThumbService;
import com.mredust.oj.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 帖子点赞接口
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestController
@RequestMapping("/post-thumb")
public class PostThumbController {
    
    @Resource
    private PostThumbService postThumbService;
    
    @Resource
    private UserService userService;
    
    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest 帖子点赞请求
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> postThumb(@RequestBody PostThumbAddRequest postThumbAddRequest) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        int result = postThumbService.postThumb(postThumbAddRequest.getPostId(), loginUser.getId());
        return Result.success(result);
    }
}
