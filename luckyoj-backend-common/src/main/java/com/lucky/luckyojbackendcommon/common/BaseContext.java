package com.lucky.luckyojbackendcommon.common;

/**
 * @author: 小飞的电脑
 * @Date: 2023/6/5 - 06 - 05 - 16:55
 * @Description: com.itheima.reggie.common
 * @version: 1.0
 */

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
