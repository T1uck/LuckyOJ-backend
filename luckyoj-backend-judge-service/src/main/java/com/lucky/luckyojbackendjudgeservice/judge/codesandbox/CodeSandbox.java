package com.lucky.luckyojbackendjudgeservice.judge.codesandbox;

import com.lucky.luckyojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.lucky.luckyojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandbox {

    /**
     * 执行代码
     *
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
