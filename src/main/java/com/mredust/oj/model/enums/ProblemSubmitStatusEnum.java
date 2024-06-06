package com.mredust.oj.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 题目提交状态枚举
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum ProblemSubmitStatusEnum {
    // 判题状态（0-待判题 1-判题中 2-成功 3-失败）
    
    WAITING("等待中", 0),
    RUNNING("判题中", 1),
    SUCCEED("成功", 2),
    FAILED("失败", 3);
    
    private final String text;
    private final Integer code;
    
}
