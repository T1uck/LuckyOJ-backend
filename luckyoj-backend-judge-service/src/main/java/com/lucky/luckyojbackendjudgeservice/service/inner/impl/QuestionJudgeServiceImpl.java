package com.lucky.luckyojbackendjudgeservice.service.inner.impl;

import com.lucky.luckyojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.lucky.luckyojbackendjudgeservice.judge.codesandbox.CodeSandboxFactory;
import com.lucky.luckyojbackendjudgeservice.service.inner.QuestionJudgeService;
import com.lucky.luckyojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.lucky.luckyojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.lucky.luckyojbackendmodel.model.dto.question.QuestionRunRequest;
import com.lucky.luckyojbackendmodel.model.entity.User;
import com.lucky.luckyojbackendmodel.model.enums.ExecuteCodeStatusEnum;
import com.lucky.luckyojbackendmodel.model.vo.QuestionRunResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author: 小飞的电脑
 * @Date: 2023/10/8 - 10 - 08 - 16:53
 * @Description: com.lucky.luckyojbackendjudgeservice.service.inner.impl
 * @version: 1.0
 */
@Service
public class QuestionJudgeServiceImpl implements QuestionJudgeService {
    @Value("${codesandbox.type:remote}")
    private String type;

    @Override
    public QuestionRunResult doQuestionRun(QuestionRunRequest questionRunRequest, User loginUser) {
        String code = questionRunRequest.getCode();
        String language = questionRunRequest.getLanguage();
        List<String> inputList = Collections.singletonList(questionRunRequest.getInput());

        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse response = codeSandbox.executeCode(executeCodeRequest);

        QuestionRunResult questionRunResult = new QuestionRunResult();
        questionRunResult.setInput(questionRunResult.getInput());
        //执行成功
        if(response.getCode().equals(ExecuteCodeStatusEnum.SUCCESS.getValue())){
            questionRunResult.setCode(ExecuteCodeStatusEnum.SUCCESS.getValue());
            questionRunResult.setOutput(response.getOutputList().toString());
        } else if(response.getCode().equals(ExecuteCodeStatusEnum.RUN_FAILED.getValue())){
            questionRunResult.setCode(ExecuteCodeStatusEnum.RUN_FAILED.getValue());
            questionRunResult.setOutput(response.getMessage());
        } else if(response.getCode().equals(ExecuteCodeStatusEnum.COMPILE_FAILED.getValue())){
            questionRunResult.setCode(ExecuteCodeStatusEnum.COMPILE_FAILED.getValue());
            questionRunResult.setOutput(response.getMessage());
        }
        return questionRunResult;
    }
}
