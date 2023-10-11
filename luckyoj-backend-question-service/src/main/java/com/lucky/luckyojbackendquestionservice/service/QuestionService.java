package com.lucky.luckyojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lucky.luckyojbackendmodel.model.dto.question.ProblemQueryRequest;
import com.lucky.luckyojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.lucky.luckyojbackendmodel.model.entity.Question;
import com.lucky.luckyojbackendmodel.model.vo.QuestionVO;
import com.lucky.luckyojbackendmodel.model.vo.SafeQuestionVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-08-07 20:58:00
*/
public interface QuestionService extends IService<Question> {


    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取查询条件problem
     * @param problemQueryRequest
     * @return
     */
    Wrapper<Question> getProblemQueryWrapper(ProblemQueryRequest problemQueryRequest,HttpServletRequest httpServletRequest);

    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 获取所有标签
     * @return
     */
    List<String> getProblemTags();

    /**
     * 对象转包装类
     * @param question
     * @return
     */
    SafeQuestionVo objToVO(Question question, Long id);

}
