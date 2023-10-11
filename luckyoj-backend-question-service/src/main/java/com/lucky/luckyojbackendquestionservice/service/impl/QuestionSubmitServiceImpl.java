package com.lucky.luckyojbackendquestionservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lucky.luckyojbackendcommon.common.ErrorCode;
import com.lucky.luckyojbackendcommon.constant.CommonConstant;
import com.lucky.luckyojbackendcommon.exception.BusinessException;
import com.lucky.luckyojbackendcommon.utils.SqlUtils;
import com.lucky.luckyojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.lucky.luckyojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.lucky.luckyojbackendmodel.model.entity.Question;
import com.lucky.luckyojbackendmodel.model.entity.QuestionSubmit;
import com.lucky.luckyojbackendmodel.model.entity.User;
import com.lucky.luckyojbackendmodel.model.enums.ProblemDifficultyEnum;
import com.lucky.luckyojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.lucky.luckyojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.lucky.luckyojbackendmodel.model.vo.QuestionSubmitVO;
import com.lucky.luckyojbackendmodel.model.vo.SubmitSummaryVo;
import com.lucky.luckyojbackendquestionservice.mapper.QuestionMapper;
import com.lucky.luckyojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.lucky.luckyojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.lucky.luckyojbackendquestionservice.service.QuestionService;
import com.lucky.luckyojbackendquestionservice.service.QuestionSubmitService;
import com.lucky.luckyojbackendserviceclient.service.JudgeFeignClient;
import com.lucky.luckyojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2023-08-07 20:58:53
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }
        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交题目
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(language);
        // 设置初始状态
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        Long questionSubmitId = questionSubmit.getId();
        // 发送消息
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
        // 执行判题服务
//        CompletableFuture.runAsync(() -> {
//            judgeFeignClient.doJudge(questionSubmitId);
//        });
        return questionSubmitId;
    }


    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象，得到 mybatis 框架支持的查询 QueryWrapper 类）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) != null, "status", status);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != questionSubmit.getUserId() && !userFeignClient.isAdmin(loginUser)) {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    @Override
    public SubmitSummaryVo getSubmitSummary(User loginUser) {
        SubmitSummaryVo summaryVo = new SubmitSummaryVo();
        //获取简单、中等、困难题目ids
        List<Long> easyId = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                        .select(Question::getId).eq(Question::getDifficulty, ProblemDifficultyEnum.EASY.getValue()))
                .stream().map(Question::getId).collect(Collectors.toList());
        List<Long> mediumId = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                        .select(Question::getId).eq(Question::getDifficulty, ProblemDifficultyEnum.MEDIUM.getValue()))
                .stream().map(Question::getId).collect(Collectors.toList());
        List<Long> hardId = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                        .select(Question::getId).eq(Question::getDifficulty, ProblemDifficultyEnum.HARD.getValue()))
                .stream().map(Question::getId).collect(Collectors.toList());
        int easyTotal = easyId.size();
        int mediumTotal = mediumId.size();
        int hardTotal = hardId.size();
        summaryVo.setEasyTotal(easyTotal);
        summaryVo.setMediumTotal(mediumTotal);
        summaryVo.setHardTotal(hardTotal);
        summaryVo.setTotal(easyTotal + mediumTotal + hardTotal);

        // 获取用户通过的简单，中等，困难题目数
        Integer easyPass = baseMapper.getPassCount(loginUser.getId(), easyId);
        Integer mediumPass = baseMapper.getPassCount(loginUser.getId(), mediumId);
        Integer hardPass = baseMapper.getPassCount(loginUser.getId(), hardId);
        summaryVo.setEasyPass(easyPass);
        summaryVo.setMediumPass(mediumPass);
        summaryVo.setHardPass(hardPass);

        //获取用户提交总数
        Integer submitCount = Math.toIntExact(baseMapper.selectCount(new LambdaQueryWrapper<QuestionSubmit>()
                .eq(QuestionSubmit::getUserId, loginUser.getId())));
        summaryVo.setSubmitCount(submitCount);
        //获取用户成功的提交
        Integer passCount = Math.toIntExact(baseMapper.selectCount(new LambdaQueryWrapper<QuestionSubmit>()
                .eq(QuestionSubmit::getUserId, loginUser.getId())
                .eq(QuestionSubmit::getStatus, QuestionSubmitStatusEnum.SUCCEED.getValue())));
        summaryVo.setPassCount(passCount);
        return summaryVo;
    }


}




