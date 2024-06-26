package com.mredust.oj.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mredust.oj.common.BaseResponse;
import com.mredust.oj.common.DeleteRequest;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.common.Result;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.model.dto.user.*;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.vo.UserVO;
import com.mredust.oj.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import static com.mredust.oj.constant.UserConstant.ADMIN_ROLE;

/**
 * 用户接口
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Resource
    private UserService userService;
    
    // region 用户基本操作
    
    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 用户 id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        String account = userRegisterRequest.getAccount();
        String password = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(account, password, checkPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        long userId = userService.userRegister(account, password, checkPassword);
        return Result.success(userId);
    }
    
    /**
     * 用户登录
     *
     * @param userLoginRequest 用户登录请求
     * @return 用户信息
     */
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        String account = userLoginRequest.getAccount();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        UserVO userVO = userService.userLogin(account, password);
        return Result.success(userVO);
    }
    
    /**
     * 用户注销
     *
     * @return 是否注销成功
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout() {
        boolean result = userService.userLogout();
        return Result.success(result);
    }
    
    /**
     * 获取当前登录用户
     *
     * @return 当前登录用户
     */
    @GetMapping("/get/login-user")
    public BaseResponse<UserVO> getLoginUser() {
        User user = userService.getLoginUser();
        return Result.success(userService.getUserVO(user));
    }
    // endregion
    
    
    // region 用户增删改查
    
    /**
     * 添加用户
     *
     * @param userAddRequest 用户添加请求
     * @return 是否添加成功
     */
    @PostMapping("/add")
    @SaCheckRole(ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        if (userAddRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        String account = userAddRequest.getAccount();
        String password = userAddRequest.getPassword();
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        long addResult = userService.addUser(userAddRequest);
        return Result.success(addResult);
    }
    
    
    /**
     * 删除用户
     *
     * @param deleteRequest 删除请求数据
     * @return 是否删除成功
     */
    @DeleteMapping("/delete")
    @SaCheckRole(ADMIN_ROLE)
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        boolean deleteResult = userService.removeById(id);
        return deleteResult ? Result.success("删除成功") : Result.fail("删除失败");
    }
    
    /**
     * 更新用户信息
     *
     * @param userUpdateRequest 更新用户信息
     * @return 是否更新成功
     */
    @PutMapping("/update")
    @SaCheckRole(ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        String account = userUpdateRequest.getAccount();
        String password = userUpdateRequest.getPassword();
        String username = userUpdateRequest.getUsername();
        String avatarUrl = userUpdateRequest.getAvatarUrl();
        if (StringUtils.isAnyBlank(account, password, username, avatarUrl)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        User user = new User();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return result ? Result.success("更新成功") : Result.fail("更新失败");
    }
    
    /**
     * 更新个人信息
     *
     * @param userUpdateMyRequest 更新个人信息
     * @return 是否更新成功
     */
    @PutMapping("/update/my")
    public BaseResponse<Boolean> updateMyUser(@RequestBody UserUpdateMyRequest userUpdateMyRequest) {
        User loginUser = userService.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        if (userUpdateMyRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        String username = userUpdateMyRequest.getUsername();
        String avatarUrl = userUpdateMyRequest.getAvatarUrl();
        if (StringUtils.isAnyBlank(username, avatarUrl)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        
        User user = new User();
        BeanUtils.copyProperties(userUpdateMyRequest, user);
        user.setId(loginUser.getId());
        boolean result = userService.updateById(user);
        return result ? Result.success("更新成功") : Result.fail("更新失败");
    }
    
    /**
     * 根据 id 获取用户
     *
     * @param id 用户id
     * @return 用户信息
     */
    @GetMapping("/get")
    @SaCheckRole(ADMIN_ROLE)
    public BaseResponse<User> getUserById(@RequestParam("id") long id) {
        if (id <= 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR);
        }
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException(ResponseCode.NOT_FOUND);
        }
        return Result.success(user);
    }
    
    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest 用户查询参数
     * @return 用户列表
     */
    @PostMapping("/list")
    @SaCheckRole(ADMIN_ROLE)
    public BaseResponse<Page<User>> getUserListByPage(@RequestBody UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        Page<User> userPage = userService.getUserListByPage(userQueryRequest);
        return Result.success(userPage);
    }
    // endregion
}
