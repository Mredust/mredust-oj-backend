package com.mredust.oj.model.enums.problem;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Mredust
 */

@Getter
@AllArgsConstructor
public enum JudgeInfoEnum {
    EXECUTION_PASS("执行通过", "Execution Pass"),
    WRONG_ANSWER("错误解答", "Wrong Answer"),
    OUT_OF_MEMORY_LIMIT("超出内存限制", "Out of Memory"),
    OUT_OF_TIME_LiMIT("超出时间限制", "Time Limit Exceeded"),
    OUT_OF_OUTPUT_LIMIT("超出输出限制", "Output Limit Exceeded"),
    COMPILE_ERROR("编译出错", "Compile Error"),
    EXECUTION_ERROR("执行出错", "Execution Error"),
    TIME_OUT("超时", "Time Out"),
    INTERNAL_ERROR("内部出错", "Internal Error");
    
    
    private final String text;
    private final String value;
    
    
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }
    
    public static JudgeInfoEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (JudgeInfoEnum anEnum : JudgeInfoEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
    
}
