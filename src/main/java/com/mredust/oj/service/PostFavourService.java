package com.mredust.oj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mredust.oj.model.entity.PostFavour;

/**
 * @author Mredust
 * @description 针对表【post_favour(帖子收藏)】的数据库操作Service
 */
public interface PostFavourService extends IService<PostFavour> {
    /**
     * 帖子收藏 / 取消收藏
     *
     * @param postId 帖子 id
     * @param userId     用户 id
     * @return -1: 收藏成功 0: 取消/收藏失败 1: 取消收藏成功
     */
    int postFavour(Long postId, Long userId);
    
    /**
     * 帖子收藏 / 取消收藏 (内部服务)
     *
     * @param postId 帖子 id
     * @param userId     用户 id
     * @return -1: 收藏成功 0: 取消/收藏失败 1: 取消收藏成功
     */
    int postFavourInner(Long postId, Long userId);
}
