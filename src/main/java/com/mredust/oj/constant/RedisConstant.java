package com.mredust.oj.constant;

/**
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
public interface RedisConstant {
    /**
     * 缓存时间
     */
    Long TIMEOUT_TTL = 1L;
    
    /**
     * 题目提交限流key
     */
    String LIMIT_KEY_PREFIX = "problem:commit:limit:";
    
    
    // region 题目管理
    
    /**
     * 题目详细
     */
    String PROBLEM_KEY = "problem:";
    
    /**
     * 题目分页
     */
    String PROBLEM_PAGE_KEY = "problem:page:";
    
    /**
     * 语言列表
     */
    String LANGUAGE_LIST_KEY = "problem:language:list";
    
    // endregion
}
