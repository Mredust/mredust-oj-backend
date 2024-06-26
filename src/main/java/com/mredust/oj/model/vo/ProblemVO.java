package com.mredust.oj.model.vo;

import com.mredust.oj.model.dto.problem.TemplateCode;
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
    private static final long serialVersionUID = 1L;
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
     * 难度(0-简单 1-中等 2-困难)
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
     * 题目提交数
     */
    private Integer submitNum;
    
    /**
     * 题目通过数
     */
    private Integer acceptedNum;
    
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
     * 题目模板代码
     */
    private List<TemplateCode> templateCode;
    
    /**
     * 判题用例（List<String[]>）
     */
    private List<String> testCase;
    
    /**
     * 判题用例（List<String[]>）
     */
    private List<String> testAnswer;
    
    /**
     * 运行时间限制（ms）
     */
    private Long runTime;
    
    /**
     * 内存限制（KB）
     */
    private Long runMemory;
    
    /**
     * 栈大小（KB）
     */
    private Long runStack;
    
    
}
