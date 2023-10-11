package com.lucky.luckyojbackendjudgeservice.controller.inner;

import com.lucky.luckyojbackendcommon.common.BaseResponse;
import com.lucky.luckyojbackendcommon.common.ErrorCode;
import com.lucky.luckyojbackendcommon.common.ResultUtils;
import com.lucky.luckyojbackendcommon.exception.BusinessException;
import com.lucky.luckyojbackendjudgeservice.judge.JudgeService;
import com.lucky.luckyojbackendjudgeservice.service.inner.QuestionJudgeService;
import com.lucky.luckyojbackendmodel.model.dto.question.QuestionRunRequest;
import com.lucky.luckyojbackendmodel.model.entity.QuestionSubmit;
import com.lucky.luckyojbackendmodel.model.entity.User;
import com.lucky.luckyojbackendmodel.model.vo.QuestionRunResult;
import com.lucky.luckyojbackendserviceclient.service.JudgeFeignClient;
import com.lucky.luckyojbackendserviceclient.service.UserFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 该服务仅内部调用，不是给前端的
 */
@RestController
@RequestMapping("/inner")
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionJudgeService questionJudgeService;

    @Resource
    private JudgeService judgeService;

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId){
        return judgeService.doJudge(questionSubmitId);
    }

    @PostMapping("/question_run")
    public BaseResponse<QuestionRunResult> doProblemRun(@RequestBody @NotNull @Valid QuestionRunRequest questionRunRequest, HttpServletRequest httpServletRequest) {
        User loginUser = userFeignClient.getLoginUser(httpServletRequest);
        //未登录
        if(loginUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        QuestionRunResult questionRunResult = questionJudgeService.doQuestionRun(questionRunRequest,loginUser);
        return ResultUtils.success(questionRunResult);
    }
}
