package com.lucky.luckyojbackendmodel.model.dto.email;

import lombok.Data;

/**
 * @author: 小飞的电脑
 * @Date: 2023/9/30 - 09 - 30 - 11:04
 * @Description: com.lucky.luckyojbackendmodel.model.dto.email
 * @version: 1.0
 */
@Data
public class LoginEmailRequest {
    private String email;

    private String code;
}
