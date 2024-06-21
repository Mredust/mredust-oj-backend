package com.mredust.oj.model.enums.problem;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 题目提交状态枚举
 *
 * @author <a href="https://github.com/Mredust">Mredust</a>
 */
@Getter
@AllArgsConstructor
public enum ProblemSubmitStatusEnum {
    // 判题状态（0-待判题 1-判题中 2-成功 3-失败）
    WAITING(0, "等待中", "未开始"),
    RUNNING(1, "判题中", "尝试过"),
    FAILED(2, "失败", "尝试过"),
    SUCCEED(3, "成功", "已解答");
    
    private final Integer code;
    private final String status;
    private final String text;
    
    
    public static ProblemSubmitStatusEnum getProblemSubmitStatusEnumByCode(Integer code) {
        return Stream.of(ProblemSubmitStatusEnum.values()).filter(problemSubmitStatusEnum -> problemSubmitStatusEnum.code.equals(code)).findFirst().orElse(null);
    }
    
    public static String getProblemSubmitTextByCode(Integer code) {
        return Objects.requireNonNull(Stream.of(ProblemSubmitStatusEnum.values())
                .filter(problemSubmitStatusEnum -> problemSubmitStatusEnum.code.equals(code))
                .findFirst().orElse(null)).text;
    }
    
}
