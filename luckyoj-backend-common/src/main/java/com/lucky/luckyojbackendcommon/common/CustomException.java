package com.lucky.luckyojbackendcommon.common;

/**
 * @author: 小飞的电脑
 * @Date: 2023/6/5 - 06 - 05 - 22:19
 * @Description: com.itheima.reggie.common
 * @version: 1.0
 */
public class CustomException extends RuntimeException{
    public CustomException(String meager){
        super(meager);
    }
}
