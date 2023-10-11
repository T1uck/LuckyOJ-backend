package com.lucky.luckyojbackendmodel.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *

 */
@Data
public class UpdatePasswordRequest implements Serializable {
    // 原先的密码
    private String originalPassword;

    // 新的密码
    private String newPassword;

    private static final long serialVersionUID = 1L;
}