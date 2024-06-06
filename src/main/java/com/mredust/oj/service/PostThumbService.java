package com.mredust.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mredust.oj.model.entity.PostThumb;

/**
 * @author Mredust
 * @description 针对表【post_thumb(帖子点赞表)】的数据库操作Service
 */
public interface PostThumbService extends IService<PostThumb> {
    
    /**
     * 点赞
     *
     * @param postId 帖子id
     * @param userId 用户id
     * @return -1取消点赞 0 取消/点赞失败 1 点赞成功
     */
    int postThumb(long postId, Long userId);
    
    
    /**
     * 帖子点赞（内部服务）
     *
     * @param postId 帖子id
     * @param userId 用户id
     * @return -1取消点赞 0 取消/点赞失败 1 点赞成功
     */
    int postThumbInner(long postId, long userId);
}
