package com.mredust.oj.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.constant.PostConstant;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.mapper.PostMapper;
import com.mredust.oj.model.dto.post.PostAddRequest;
import com.mredust.oj.model.dto.post.PostQueryRequest;
import com.mredust.oj.model.dto.post.PostUpdateRequest;
import com.mredust.oj.model.entity.Post;
import com.mredust.oj.model.entity.PostFavour;
import com.mredust.oj.model.entity.PostThumb;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.vo.PostVO;
import com.mredust.oj.model.vo.UserVO;
import com.mredust.oj.service.PostService;
import com.mredust.oj.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Mredust
 * @description 针对表【post(帖子表)】的数据库操作Service实现
 * @createDate 2024-06-05 12:40:54
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    
    @Resource
    private UserService userService;
    
    /**
     * 新增帖子
     *
     * @param postAddRequest
     * @return 新增的帖子id
     */
    @Override
    public long addPost(PostAddRequest postAddRequest, long userId) {
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        validPost(post);
        post.setUserId(userId);
        boolean result = this.save(post);
        if (!result) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }
        return post.getId();
    }
    
    /**
     * 更新帖子
     *
     * @param postUpdateRequest 更新帖子请求
     * @return 是否更新成功
     */
    @Override
    public boolean updatePost(PostUpdateRequest postUpdateRequest) {
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest, post);
        List<String> tags = postUpdateRequest.getTags();
        if (tags != null) {
            post.setTags(JSONUtil.toJsonStr(tags));
        }
        validPost(post);
        boolean result = this.updateById(post);
        if (!result) {
            throw new BusinessException(ResponseCode.SYSTEM_ERROR);
        }
        return true;
    }
    
    /**
     * 校验
     *
     * @param post 帖子数据
     */
    private void validPost(Post post) {
        if (post == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        String title = post.getTitle();
        String content = post.getContent();
        if (StringUtils.isAnyBlank(title, content)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > PostConstant.TITLE_MAX_LENGTH) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > PostConstant.CONTENT_MAX_LENGTH) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "内容过长");
        }
    }
    
    /**
     * 获取帖子
     *
     * @param post 帖子
     * @return 帖子视图对象
     */
    @Override
    public PostVO getPostById(Post post) {
        PostVO postVO = PostVO.objToVo(post);
        long postId = post.getId();
        Long userId = post.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        postVO.setUser(userVO);
        // 是否点赞
        PostThumb postThumb = Db.lambdaQuery(PostThumb.class)
                .eq(PostThumb::getPostId, postId)
                .eq(PostThumb::getUserId, userId)
                .one();
        postVO.setHasThumb(postThumb != null);
        // 是否收藏
        PostFavour postFavour = Db.lambdaQuery(PostFavour.class)
                .eq(PostFavour::getPostId, postId)
                .eq(PostFavour::getUserId, userId)
                .one();
        postVO.setHasFavour(postFavour != null);
        return postVO;
    }
    
    /**
     * 分页获取帖子列表
     *
     * @param postQueryRequest 查询条件
     * @return 帖子分页对象
     */
    @Override
    public Page<Post> getPostListByPage(PostQueryRequest postQueryRequest) {
        String searchText = postQueryRequest.getSearchText();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tagList = postQueryRequest.getTags();
        Long userId = postQueryRequest.getUserId();
        return Db.lambdaQuery(Post.class)
                .like(StringUtils.isNotBlank(searchText), Post::getTitle, searchText).or()
                .like(StringUtils.isNotBlank(searchText), Post::getContent, searchText)
                .like(StringUtils.isNotBlank(title), Post::getTitle, title)
                .like(StringUtils.isNotBlank(content), Post::getContent, content)
                .like(CollUtil.isNotEmpty(tagList), Post::getTags, JSONUtil.toJsonStr(tagList))
                .eq(ObjectUtils.isNotEmpty(userId), Post::getUserId, userId)
                .page(new Page<>(postQueryRequest.getPageNum(), postQueryRequest.getPageSize()));
    }
    
    /**
     * 分页获取帖子视图对象
     *
     * @param postQueryRequest 查询条件
     * @param request          请求
     * @return 帖子视图对象分页
     */
    @Override
    public Page<PostVO> getPostVoPage(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        Page<Post> postPage = getPostListByPage(postQueryRequest);
        List<Post> postList = postPage.getRecords();
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollUtil.isEmpty(postList)) {
            return postVOPage;
        }
        // 关联查询用户信息
        Set<Long> userIdSet = postList.stream().map(Post::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
        // 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> postIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> postIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUser(request);
        if (loginUser != null) {
            Set<Long> postIdSet = postList.stream().map(Post::getId).collect(Collectors.toSet());
            // 获取点赞
            Db.lambdaQuery(PostThumb.class)
                    .in(PostThumb::getPostId, postIdSet)
                    .eq(PostThumb::getUserId, loginUser.getId())
                    .list()
                    .forEach(postThumb -> postIdHasThumbMap.put(postThumb.getPostId(), true));
            // 获取收藏
            Db.lambdaQuery(PostFavour.class)
                    .in(PostFavour::getPostId, postIdSet)
                    .eq(PostFavour::getUserId, loginUser.getId())
                    .list()
                    .forEach(postFavour -> postIdHasFavourMap.put(postFavour.getPostId(), true));
        }
        // 填充信息
        List<PostVO> postVOList = postList.stream().map(post -> {
            PostVO postVO = PostVO.objToVo(post);
            Long userId = post.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            postVO.setUser(userService.getUserVO(user));
            postVO.setHasThumb(postIdHasThumbMap.getOrDefault(post.getId(), false));
            postVO.setHasFavour(postIdHasFavourMap.getOrDefault(post.getId(), false));
            return postVO;
        }).collect(Collectors.toList());
        postVOPage.setRecords(postVOList);
        return postVOPage;
    }
}




