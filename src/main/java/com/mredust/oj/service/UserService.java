package com.mredust.oj.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mredust.oj.model.dto.user.UserAddRequest;
import com.mredust.oj.model.dto.user.UserQueryRequest;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.vo.UserVO;

/**
 * @author Mredust
 * @description 针对表【user(用户表)】的数据库操作Service
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     *
     * @param account       用户账户
     * @param password      用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String account, String password, String checkPassword);
    
    /**
     * 用户登录
     *
     * @param account  用户账户
     * @param password 用户密码
     * @return 用户信息
     */
    UserVO userLogin(String account, String password);
    
    
    /**
     * 获取脱敏用户信息
     *
     * @param user 用户
     * @return 脱敏用户信息
     */
    UserVO getUserVO(User user);
    
    /**
     * 用户注销
     *
     * @return 是否注销成功
     */
    boolean userLogout();
    
    /**
     * 获取当前登录用户
     *
     * @return 用户
     */
    User getLoginUser();
    
    /**
     * 添加用户
     *
     * @param userAddRequest 用户添加请求
     * @return 用户 id
     */
    long addUser(UserAddRequest userAddRequest);
    
    /**
     * 获取用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @return 用户列表
     */
    Page<User> getUserListByPage(UserQueryRequest userQueryRequest);
}
