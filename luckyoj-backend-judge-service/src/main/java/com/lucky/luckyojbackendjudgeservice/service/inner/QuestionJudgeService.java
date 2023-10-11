package com.lucky.luckyojbackendjudgeservice.service.inner;

import com.lucky.luckyojbackendmodel.model.dto.question.QuestionRunRequest;
import com.lucky.luckyojbackendmodel.model.entity.User;
import com.lucky.luckyojbackendmodel.model.vo.QuestionRunResult;

/**
 * @author: 小飞的电脑
 * @Date: 2023/10/8 - 10 - 08 - 16:52
 * @Description: com.lucky.luckyojbackendjudgeservice.service.inner
 * @version: 1.0
 */
public interface QuestionJudgeService {
    QuestionRunResult doQuestionRun(QuestionRunRequest questionRunRequest, User loginUser);
}
