package com.mredust.oj.model.dto.problem;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@NotNull
public class ProblemUpdateRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @Min(1)
    private Long id;
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
     * 题目模板代码
     */
    private String templateCode;
    
    /**
     * 难度(0-简单 1-中等 2-困难)
     */
    private Integer difficulty;
    
    /**
     * 标签列表（json 数组）
     */
    private String tags;
    
    /**
     * 判题用例（List<String[]>）
     */
    private List<String[]> testCase;
    
    /**
     * 判题用例答案（List<String[]>）
     */
    private List<String> testAnswer;
    
    /**
     * 运行时间限制（ms）
     */
    private Integer runTime;
    
    /**
     * 内存限制（KB）
     */
    private Integer runMemory;
    
    /**
     * 栈大小（KB）
     */
    private Integer runStack;
    
    
    /**
     * 创建用户 id
     */
    private Long userId;
}
