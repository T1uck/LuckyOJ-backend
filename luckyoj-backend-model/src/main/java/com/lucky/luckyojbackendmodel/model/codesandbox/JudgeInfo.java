package com.lucky.luckyojbackendmodel.model.codesandbox;

import lombok.Data;

/**
 * 判题信息
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间（KB）
     */
    private Long time;

    /**
     * 通过用例数
     */
    private Integer pass;
    /**
     * 总用例数
     */
    private Integer total;

    /**
     * 状态
     */
    private String status;

    private String input;
    private String output;
    private String expectedOutput;
}
