package com.mredust.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.mapper.PostFavourMapper;
import com.mredust.oj.model.entity.Post;
import com.mredust.oj.model.entity.PostFavour;
import com.mredust.oj.service.PostFavourService;
import com.mredust.oj.service.PostService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Mredust
 * @description 针对表【post_favour(帖子收藏)】的数据库操作Service实现
 * @createDate 2024-06-05 12:40:58
 */
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour> implements PostFavourService {
    
    @Resource
    private PostService postService;
    
    /**
     * 帖子收藏 / 取消收藏
     *
     * @param postId 帖子 id
     * @param userId 用户 id
     * @return -1: 收藏成功 0: 取消/收藏失败 1: 取消收藏成功
     */
    @Override
    public int postFavour(Long postId, Long userId) {
        // 判断是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        // 每个用户串行帖子收藏
        PostFavourService postFavourService = (PostFavourService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postFavourService.postFavourInner(postId, userId);
        }
    }
    
    /**
     * 帖子收藏 / 取消收藏 (内部服务)
     *
     * @param postId 帖子 id
     * @param userId 用户 id
     * @return -1: 收藏成功 0: 取消/收藏失败 1: 取消收藏成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int postFavourInner(Long postId, Long userId) {
        PostFavour postFavour = new PostFavour();
        postFavour.setUserId(userId);
        postFavour.setPostId(postId);
        QueryWrapper<PostFavour> wrapper = new QueryWrapper<>(postFavour);
        PostFavour oldPostFavour = this.getOne(wrapper);
        boolean result;
        // 已收藏
        if (oldPostFavour != null) {
            result = this.remove(wrapper);
            if (result) {
                // 帖子收藏数 - 1
                result = Db.lambdaUpdate(Post.class)
                        .eq(postId != 0, Post::getId, postId)
                        .gt(Post::getFavourNum, 0)
                        .setSql("favour_num = favour_num - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ResponseCode.SYSTEM_ERROR);
            }
        } else {
            // 未帖子收藏
            result = this.save(postFavour);
            if (result) {
                // 帖子收藏数 + 1
                result = Db.lambdaUpdate(Post.class)
                        .eq(postId != 0, Post::getId, postId)
                        .gt(Post::getFavourNum, 0)
                        .setSql("favour_num = favour_num + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ResponseCode.SYSTEM_ERROR);
            }
        }
    }
}




