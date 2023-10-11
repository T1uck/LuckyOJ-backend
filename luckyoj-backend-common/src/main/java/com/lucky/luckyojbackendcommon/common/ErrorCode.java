package com.lucky.luckyojbackendcommon.common;

/**
 * 自定义错误码
 *

 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    API_REQUEST_ERROR(50010, "接口调用失败"),

    // 登录
    CODE_EXCEPTION(10001,"验证码获取频率太高，请稍后再试"),
    WRONG_CODE(10002, "验证码错误"),
    ACCOUNT_NOT_EXIST(10003, "账号不存在"),
    WRONG_PASSWORD(10004, "账号或密码错误"),
    THIRD_PARTY_EXCEPTION(10005, "第三方登录异常"),
    NOT_LOGIN(10006, "未登录"),

    //OJ相关
    SUBMIT_ERROR(20001, "提交判题失败"),
    //代码沙箱
    DANGER_CODE(21001, "危险代码");
    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
