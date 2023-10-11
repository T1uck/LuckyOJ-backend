package com.lucky.luckyojbackendmodel.model.vo;

import lombok.Data;

@Data
public class SubmitSummaryVo {
    //题库总数
    private Integer total;

    //简单
    // 简单通过人数
    private Integer easyPass;
    // 简单测试总人数
    private Integer easyTotal;

    //中等
    // 中等通过人数
    private Integer mediumPass;
    // 中等测试人数
    private Integer mediumTotal;

    //困难
    // 困难通过人数
    private Integer hardPass;
    // 困难测试人数
    private Integer hardTotal;

    //提交总数
    private Integer submitCount;
    //通过总数
    private Integer passCount;
}
