package com.mredust.oj.constant;


/**
 * 用户常量
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface UserConstant {
    // region 增删改查
    /**
     * 用户注册账号最小长度
     */
    int USER_ACCOUNT_MIN_LENGTH = 4;
    
    /**
     * 用户注册密码最小长度
     */
    int USER_PASSWORD_MIN_LENGTH = 4;
    
    /**
     * 用户名正则
     */
    String USERNAME_REGEX = "^[\\u4e00-\\u9fa5a-zA-Z0-9]+$";
    /**
     * 账号正则
     */
    String ACCOUNT_REGEX = "^\\w+$";
    /**
     * 邮箱正则
     */
    String EMAIL_REGEX = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    
    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "12345678";
    
    /**
     * 默认头像
     */
    String DEFAULT_AVATAR = "https://raw.githubusercontent.com/Mredust/images/main/file/6a354be99f4589a9dc6f8740e17d505.png";
    
    // endregion
    
    /**
     * 用户登录态键
     */
    String USER_LOGIN_KEY = "user_login";
    
    //  region 权限
    
    /**
     * 默认角色
     */
    String DEFAULT_ROLE = "user";
    
    /**
     * 管理员角色
     */
    String ADMIN_ROLE = "admin";
    
    /**
     * 被封号
     */
    String BAN_ROLE = "ban";
    
    // endregion
    
    // region加密
    /**
     * 加密盐
     */
    String SALT = "mredust";
    /**
     * 公钥/私钥
     */
    String ACCESS_SECRET_KEY = "access_secret";
}
