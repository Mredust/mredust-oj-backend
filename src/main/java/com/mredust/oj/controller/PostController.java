package com.mredust.oj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mredust.oj.annotation.AuthCheck;
import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.DeleteRequest;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.dto.post.PostAddRequest;
import com.mredust.oj.model.dto.post.PostQueryRequest;
import com.mredust.oj.model.dto.post.PostUpdateRequest;
import com.mredust.oj.model.entity.Post;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.user.RoleEnum;
import com.mredust.oj.model.vo.PostVO;
import com.mredust.oj.service.PostService;
import com.mredust.oj.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import static com.mredust.oj.constant.UserConstant.ADMIN_ROLE;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestController
@RequestMapping("/post")
public class PostController {
    
    @Resource
    private PostService postService;
    @Resource
    private UserService userService;
    
    // region 增删改查
    
    /**
     * 新增帖子
     *
     * @param postAddRequest 新增帖子请求
     * @param request        请求
     * @return 新增的帖子id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        User loginUser = userService.getLoginUser(request);
        long postId = postService.addPost(postAddRequest, loginUser.getId());
        return Result.success(postId);
    }
    
    /**
     * 删除帖子
     *
     * @param deleteRequest 删除请求
     * @param request       请求
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deletePost(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        Long postId = deleteRequest.getId();
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        User loginUser = userService.getLoginUser(request);
        if (!loginUser.getId().equals(post.getUserId()) || !RoleEnum.ADMIN.getCode().equals(loginUser.getRole())) {
            throw new BusinessException(ResponseCode.NO_AUTH);
        }
        boolean result = postService.removeById(postId);
        return Result.success(result);
    }
    
    /**
     * 更新帖子
     *
     * @param postUpdateRequest
     * @return
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest) {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        boolean result = postService.updatePost(postUpdateRequest);
        return Result.success(result);
    }
    
    /**
     * 根据 id 获取帖子
     *
     * @param id 帖子id
     * @return 帖子信息
     */
    @GetMapping("/get")
    public BaseResponse<PostVO> getPostById(long id) {
        if (id <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return Result.success(postService.getPostById(post));
    }
    
    /**
     * 分页获取帖子列表
     *
     * @param postQueryRequest 查询条件
     * @return 帖子分页对象
     */
    @PostMapping("/list")
    @AuthCheck(role = ADMIN_ROLE)
    public BaseResponse<Page<Post>> getPostListByPage(@RequestBody PostQueryRequest postQueryRequest) {
        if (postQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        Page<Post> list = postService.getPostListByPage(postQueryRequest);
        return Result.success(list);
    }
    
    /**
     * 分页获取列表（封装类）
     *
     * @param postQueryRequest 查询条件
     * @param request          请求
     * @return 帖子分页对象
     */
    @PostMapping("/list/vo")
    public BaseResponse<Page<PostVO>> getPostVoPage(@RequestBody PostQueryRequest postQueryRequest, HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        Page<PostVO> list = postService.getPostVoPage(postQueryRequest, request);
        return Result.success(list);
    }
    
    // endregion
}
