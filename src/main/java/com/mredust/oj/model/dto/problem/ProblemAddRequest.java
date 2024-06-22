package com.mredust.oj.model.dto.problem;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
    
    private static final long serialVersionUID = 1L;
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
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 题目模板代码(json)
     */
    private List<TemplateCode> templateCode;
    
    /**
     * 难度(0-简单 1-中等 2-困难)
     */
    private Integer difficulty;
    
    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;
    
    /**
     * 判题用例（List<String>）
     */
    private List<String> testCase;
    
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
    
    
}
