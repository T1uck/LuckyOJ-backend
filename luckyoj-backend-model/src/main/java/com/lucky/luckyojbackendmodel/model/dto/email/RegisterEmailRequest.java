package com.lucky.luckyojbackendmodel.model.dto.email;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author: 小飞的电脑
 * @Date: 2023/9/30 - 09 - 30 - 11:04
 * @Description: com.lucky.luckyojbackendmodel.model.dto.email
 * @version: 1.0
 */
@Data
public class RegisterEmailRequest {

    private static final long serialVersionUID = 3191241716373120793L;
    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 确认密码
     */
    @NotEmpty(message = "确认密码不能为空")
    private String checkPassword;

    /**
     * 验证码
     */
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
