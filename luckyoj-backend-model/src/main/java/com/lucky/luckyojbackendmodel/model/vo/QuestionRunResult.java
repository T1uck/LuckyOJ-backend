package com.lucky.luckyojbackendmodel.model.vo;

import lombok.Data;

@Data
public class QuestionRunResult {
    /**
     * 执行状态
     */
    private Integer code;
    /**
     * 输入
     */
    private String input;
    /**
     * 执行结果
     */
    private String output;

}
