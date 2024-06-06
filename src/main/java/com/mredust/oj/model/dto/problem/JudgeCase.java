package com.mredust.oj.model.dto.problem;

import lombok.Data;

/**
 * 题目用例
 * @author Mredust
 */
@Data
public class JudgeCase {

    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;
}
