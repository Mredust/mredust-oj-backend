package com.mredust.oj.judge.codesandbox.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 代码执行响应
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecuteCodeResponse {
    
    /**
     * 输出列表
     */
    private List<String> outputList;
    
    /**
     * 接口信息
     */
    private String message;
    
    /**
     * 执行状态
     */
    private Integer status;
    
    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;
}
