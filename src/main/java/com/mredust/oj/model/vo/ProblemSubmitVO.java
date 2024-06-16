package com.mredust.oj.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目提交封装类
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class ProblemSubmitVO implements Serializable {
    /**
     * id
     */
    private Long id;
    
    /**
     * 编程语言
     */
    private String language;
    
    /**
     * 用户代码
     */
    private String code;
    
    /**
     * 判题信息（json 对象）
     */
    private JudgeInfo judgeInfo;
    
    /**
     * 判题状态（0-待判题 1-判题中 2-成功 3-失败）
     */
    private Integer status;
    
    /**
     * 题目id
     */
    private Long problemId;
    
    /**
     * 创建用户id
     */
    private Long userId;
    
    
    private static final long serialVersionUID = 1L;
    
}
