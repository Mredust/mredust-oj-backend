package com.mredust.oj.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.mredust.oj.common.ResponseCode;
import com.mredust.oj.exception.BusinessException;
import com.mredust.oj.mapper.UserMapper;
import com.mredust.oj.model.dto.user.UserAddRequest;
import com.mredust.oj.model.dto.user.UserQueryRequest;
import com.mredust.oj.model.entity.User;
import com.mredust.oj.model.enums.AccountStatusEnum;
import com.mredust.oj.model.vo.UserVO;
import com.mredust.oj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.mredust.oj.constant.UserConstant.*;

/**
 * @author Mredust
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-06-04 17:55:28
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    /**
     * 用户注册
     *
     * @param account       用户账户
     * @param password      用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    @Override
    public long userRegister(String account, String password, String checkPassword) {
        // 二次校验
        if (!Pattern.matches(ACCOUNT_REGEX, account)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "不能包含特殊字符");
        }
        if (account.length() < USER_ACCOUNT_MIN_LENGTH) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户账号过短");
        }
        if (password.length() < USER_PASSWORD_MIN_LENGTH || checkPassword.length() < USER_PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户密码过短");
        }
        if (!password.equals(checkPassword)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "两次密码不一致");
        }
        synchronized (account.intern()) {
            // 账户不能重复
            Long count = Db.lambdaQuery(User.class).eq(User::getAccount, account).count();
            if (count > 0) {
                throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号已存在");
            }
            // 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            // 插入数据
            User user = new User();
            user.setAccount(account);
            user.setPassword(encryptPassword);
            fillDefaultData(user);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ResponseCode.SYSTEM_ERROR, "注册失败，服务器内容异常");
            }
            return user.getId();
        }
    }
    
    /**
     * 用户登录
     *
     * @param account  用户账户
     * @param password 用户密码
     * @param request  请求
     * @return 用户信息
     */
    @Override
    public UserVO userLogin(String account, String password, HttpServletRequest request) {
        // 校验
        if (StringUtils.isAnyBlank(account, password)) {
            throw new BusinessException(ResponseCode.PARAMS_NULL);
        }
        if (account.length() < USER_ACCOUNT_MIN_LENGTH || password.length() < USER_PASSWORD_MIN_LENGTH || !Pattern.matches(ACCOUNT_REGEX, account)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户账号或密码错误");
        }
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        // 查询用户是否存在
        User user = Db.lambdaQuery(User.class)
                .eq(User::getAccount, account)
                .eq(User::getPassword, encryptPassword)
                .one();
        // 用户不存在
        if (user == null) {
            log.info("user login failed, account cannot match password");
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "账户不存在或密码错误");
        }
        Integer status = user.getStatus();
        if (AccountStatusEnum.BAN.getCode().equals(status)) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "账户已被封禁");
        }
        request.getSession().setAttribute(USER_LOGIN_KEY, user);
        return this.getUserVO(user);
    }
    
    
    /**
     * 获取脱敏的用户信息
     *
     * @param user 用户信息
     * @return 脱敏的用户信息
     */
    @Override
    public UserVO getUserVO(User user) {
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }
    
    /**
     * 用户注销
     *
     * @param request 请求
     * @return 是否注销成功
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_KEY) == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_KEY);
        return true;
    }
    
    /**
     * 获取当前登录用户
     *
     * @param request 请求
     * @return 用户
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_KEY);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        currentUser = this.getById(currentUser.getId());
        if (currentUser == null) {
            throw new BusinessException(ResponseCode.NOT_LOGIN);
        }
        return currentUser;
    }
    
    /**
     * 添加用户
     *
     * @param userAddRequest 用户添加请求
     * @return 用户 id
     */
    @Override
    public long addUser(UserAddRequest userAddRequest) {
        String account = userAddRequest.getAccount();
        String password = userAddRequest.getPassword();
        String username = userAddRequest.getUsername();
        if (!Pattern.matches(ACCOUNT_REGEX, account) || (StringUtils.isNotBlank(username) && !Pattern.matches(USERNAME_REGEX, username))) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "不能包含特殊字符");
        }
        if (account.length() < USER_ACCOUNT_MIN_LENGTH) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户账号过短");
        }
        if (password.length() < USER_PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "用户密码过短");
        }
        // 账户不能重复
        Long count = Db.lambdaQuery(User.class).eq(User::getAccount, account).count();
        if (count > 0) {
            throw new BusinessException(ResponseCode.PARAMS_ERROR, "账号已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        fillDefaultData(user);
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        user.setPassword(encryptPassword);
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ResponseCode.FAIL, "新增用户失败");
        }
        return user.getId();
    }
    
    /**
     * 注册和新增用户默认填充数据
     */
    private void fillDefaultData(User user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String avatarUrl = user.getAvatarUrl();
        // 默认用户
        if (StringUtils.isBlank(username)) {
            String s = UUID.randomUUID().toString().split("-")[0];
            String defaultUsername = String.format("user%s", s);
            user.setUsername(defaultUsername);
        }
        // 默认密码
        if (StringUtils.isBlank(password)) {
            user.setPassword(DEFAULT_PASSWORD);
        }
        // 默认头像
        if (StringUtils.isBlank(avatarUrl)) {
            user.setAvatarUrl(DEFAULT_AVATAR);
        }
        
    }
    
    /**
     * 获取用户列表
     *
     * @param userQueryRequest 用户查询请求
     * @return 用户列表
     */
    @Override
    public Page<User> getUserListByPage(UserQueryRequest userQueryRequest) {
        Long id = userQueryRequest.getId();
        String account = userQueryRequest.getAccount();
        String username = userQueryRequest.getUsername();
        Integer sex = userQueryRequest.getSex();
        Integer status = userQueryRequest.getStatus();
        Integer role = userQueryRequest.getRole();
        long pageNum = userQueryRequest.getPageNum();
        long pageSize = userQueryRequest.getPageSize();
        
        Page<User> userPage = new Page<>(pageNum, pageSize);
        return Db.lambdaQuery(User.class)
                .eq(id != null, User::getId, id)
                .like(StringUtils.isNotBlank(account), User::getAccount, account)
                .like(StringUtils.isNotBlank(username), User::getUsername, username)
                .eq(role != null, User::getRole, role)
                .eq(sex != null, User::getSex, sex)
                .eq(status != null, User::getStatus, status)
                .page(userPage);
    }
}




