package com.mredust.oj.model.vo;

import com.mredust.oj.model.dto.problem.JudgeConfig;
import lombok.Data;

/**
 * 判题信息
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Data
public class JudgeInfo {
    private Integer status;
    private String message;
    private JudgeConfig judgeConfig;
}
