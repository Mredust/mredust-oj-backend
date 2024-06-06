package com.mredust.oj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.mapper.PostThumbMapper;
import com.mredust.oj.model.entity.Post;
import com.mredust.oj.model.entity.PostThumb;
import com.mredust.oj.service.PostService;
import com.mredust.oj.service.PostThumbService;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author Mredust
 * @description 针对表【post_thumb(帖子点赞表)】的数据库操作Service实现
 * @createDate 2024-06-05 12:41:01
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb> implements PostThumbService {
    @Resource
    private PostService postService;
    
    /**
     * 点赞
     *
     * @param postId 帖子id
     * @param userId 用户id
     * @return
     */
    @Override
    public int postThumb(long postId, Long userId) {
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        PostThumbService postThumbServiceProxy = (PostThumbService) AopContext.currentProxy();
        synchronized (String.valueOf(userId).intern()) {
            return postThumbServiceProxy.postThumbInner(postId, userId);
        }
    }
    
    
    /**
     * 帖子点赞（内部服务）
     *
     * @param postId 帖子id
     * @param userId 用户id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int postThumbInner(long postId, long userId) {
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        QueryWrapper<PostThumb> wrapper = new QueryWrapper<>(postThumb);
        PostThumb oldPostThumb = this.getOne(wrapper);
        boolean result;
        // 已点赞
        if (oldPostThumb != null) {
            result = this.remove(wrapper);
            if (result) {
                // 点赞数 - 1
                result = Db.lambdaUpdate(Post.class)
                        .eq(postId != 0, Post::getId, postId)
                        .gt(Post::getThumbNum, 0)
                        .setSql("thumb_num = thumb_num - 1")
                        .update();
                return result ? -1 : 0;
            } else {
                throw new BusinessException(ResponseCode.SYSTEM_ERROR);
            }
        } else {
            // 未点赞
            result = this.save(postThumb);
            if (result) {
                // 点赞数 + 1
                result = Db.lambdaUpdate(Post.class)
                        .eq(postId != 0, Post::getId, postId)
                        .setSql("thumb_num = thumb_num + 1")
                        .update();
                return result ? 1 : 0;
            } else {
                throw new BusinessException(ResponseCode.SYSTEM_ERROR);
            }
        }
    }
}




