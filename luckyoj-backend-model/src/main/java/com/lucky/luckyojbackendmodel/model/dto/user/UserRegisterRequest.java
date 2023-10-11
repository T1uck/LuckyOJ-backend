package com.lucky.luckyojbackendmodel.model.dto.user;

import lombok.Data;

import java.io.Serializable;
import javax.validation.constraints.NotEmpty;

/**
 * 用户注册请求体
 *

 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;

    @NotEmpty(message = "确认密码不能为空")
    private String checkPassword;
}
