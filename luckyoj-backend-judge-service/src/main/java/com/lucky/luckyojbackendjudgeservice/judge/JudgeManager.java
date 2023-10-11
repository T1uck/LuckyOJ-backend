package com.lucky.luckyojbackendjudgeservice.judge;

import com.lucky.luckyojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.lucky.luckyojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.lucky.luckyojbackendjudgeservice.judge.strategy.JudgeContext;
import com.lucky.luckyojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.lucky.luckyojbackendmodel.model.codesandbox.JudgeInfo;
import com.lucky.luckyojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
