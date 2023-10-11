package com.lucky.luckyojbackendquestionservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lucky.luckyojbackendmodel.model.entity.QuestionSubmit;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @description 针对表【question_submit(题目提交)】的数据库操作Mapper
* @createDate 2023-08-07 20:58:53
* @Entity com.lucky.luckyoj.model.entity.QuestionSubmit
*/
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {
    Integer getPassCount(@Param("userId") Long userId, @Param("id") List<Long> id);
}




