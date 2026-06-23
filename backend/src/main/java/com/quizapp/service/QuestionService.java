package com.quizapp.service;

import com.quizapp.common.Result;
import com.quizapp.dto.BatchImportDTO;
import com.quizapp.dto.QuestionQueryDTO;
import com.quizapp.vo.PageVO;
import com.quizapp.vo.QuestionVO;

import java.util.List;
import java.util.Map;

/**
 * 题目服务接口
 * <p>提供题目查询、详情、导入、统计等业务操作</p>
 *
 * @author quizapp
 */
public interface QuestionService {

    /**
     * 分页查询题目列表
     *
     * @param dto 查询条件（关键词、题型、分类、分页参数）
     * @return 分页题目列表
     */
    Result<PageVO<QuestionVO>> queryQuestions(QuestionQueryDTO dto);

    /**
     * 获取题目详情
     *
     * @param questionId 题目ID
     * @param userId     当前用户ID（用于判断收藏/错题状态），可为null
     * @return 题目详情
     */
    Result<QuestionVO> getQuestionDetail(Long questionId, Long userId);

    /**
     * 根据ID列表批量获取题目
     *
     * @param ids 题目ID列表
     * @return 题目列表
     */
    Result<List<QuestionVO>> getQuestionsByIds(List<Long> ids);

    /**
     * 批量导入答案数据
     *
     * @param items 导入数据列表
     * @return 操作结果
     */
    Result<Void> importAnswers(List<BatchImportDTO> items);

    /**
     * 统计指定题型的题目数量
     *
     * @param questionType 题目类型
     * @return 题目数量
     */
    Result<Long> countByType(String questionType);

    /**
     * 获取各分类下的题目统计
     *
     * @return 分类统计列表
     */
    Result<List<Map<String, Object>>> getCategoryStats();
}