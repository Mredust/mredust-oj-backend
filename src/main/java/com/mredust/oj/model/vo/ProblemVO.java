package com.mredust.oj.model.vo;

import com.mredust.oj.model.dto.problem.JudgeCase;
import com.mredust.oj.model.dto.problem.JudgeConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目封装类
 *
 * @author Mredust
 * @TableName question
 */
@Data
public class ProblemVO implements Serializable {
    /**
     * id
     */
    private Long id;
    
    /**
     * 标题
     */
    private String title;
    
    /**
     * 内容
     */
    private String content;
    
    /**
     * 难度
     */
    private Integer difficulty;
    
    /**
     * 状态（已通过、尝试过、未开始）
     */
    private String status;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    /**
     * 题解
     */
    private String answer;
    
    /**
     * 题目提交数
     */
    private Integer submitNum;
    
    /**
     * 题目通过数
     */
    private Integer acceptedNum;
    
    /**
     * 测试用例（json 对象）
     */
    private List<JudgeCase> judgeCase;
    
    /**
     * 判题配置（json 对象）
     */
    private JudgeConfig judgeConfig;
    
    /**
     * 点赞数
     */
    private Integer thumbNum;
    
    /**
     * 收藏数
     */
    private Integer favourNum;
    
    /**
     * 创建者
     */
    private Long userId;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    
    private static final long serialVersionUID = 1L;
}
