package com.mredust.oj.model.dto.problem;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@ApiModel(value = "新增题目请求")
@NotNull
public class ProblemAddRequest implements Serializable {
    /**
     * 标题
     */
    @NotBlank
    @Max(message = "标题过长", value = 50)
    private String title;
    
    /**
     * 内容
     */
    @NotBlank
    @Max(message = "内容过长", value = 5000)
    private String content;
    
    /**
     * 难度
     */
    @NotBlank
    private String difficulty;
    
    /**
     * 标签列表
     */
    @NotNull
    private List<String> tags;
    
    /**
     * 题目答案
     */
    private String answer;
    
    /**
     * 判题用例
     */
    @NotNull
    private List<JudgeCase> judgeCase;
    
    /**
     * 判题配置
     */
    @NotNull
    private JudgeConfig judgeConfig;
    
    private static final long serialVersionUID = 1L;
}
