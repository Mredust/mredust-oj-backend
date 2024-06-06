package com.mredust.oj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mredust.oj.model.dto.post.PostAddRequest;
import com.mredust.oj.model.dto.post.PostQueryRequest;
import com.mredust.oj.model.dto.post.PostUpdateRequest;
import com.mredust.oj.model.entity.Post;
import com.mredust.oj.model.vo.PostVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Mredust
 * @description 针对表【post(帖子表)】的数据库操作Service
 */
public interface PostService extends IService<Post> {
    
    
    /**
     * 新增帖子
     *
     * @param postAddRequest 新增帖子请求
     * @param userId         用户id
     * @return 新增的帖子id
     */
    long addPost(PostAddRequest postAddRequest, long userId);
    
    /**
     * 更新帖子
     *
     * @param postUpdateRequest 更新帖子请求
     * @return 是否更新成功
     */
    boolean updatePost(PostUpdateRequest postUpdateRequest);
    
    /**
     * 获取帖子
     *
     * @param post 帖子
     * @return 帖子视图对象
     */
    PostVO getPostById(Post post);
    
    /**
     * 分页获取帖子列表
     *
     * @param postQueryRequest 查询条件
     * @return 帖子分页对象
     */
    Page<Post> getPostListByPage(PostQueryRequest postQueryRequest);
    
    /**
     * 分页获取帖子视图对象
     *
     * @param postQueryRequest 查询条件
     * @param request          请求
     * @return 帖子视图对象分页
     */
    Page<PostVO> getPostVoPage(PostQueryRequest postQueryRequest, HttpServletRequest request);
}
