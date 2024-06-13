package com.mredust.oj.model.dto.problem;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
@NotNull
public class ProblemAddRequest implements Serializable {
    /**
     * 标题
     */
    @NotBlank
    @Length(message = "标题过长", max = 50)
    private String title;
    
    /**
     * 内容
     */
    @NotBlank
    @Length(message = "内容过长", max = 5000)
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
