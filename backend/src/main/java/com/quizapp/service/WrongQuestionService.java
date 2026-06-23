package com.quizapp.service;

import com.quizapp.common.Result;
import com.quizapp.vo.PageVO;
import com.quizapp.vo.WrongQuestionVO;

import java.util.List;

/**
 * 错题本服务接口
 * <p>提供错题查询、移除、清空等业务操作</p>
 *
 * @author quizapp
 */
public interface WrongQuestionService {

    /**
     * 分页查询用户错题列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页错题列表
     */
    Result<PageVO<WrongQuestionVO>> getWrongQuestions(Long userId, int page, int size);

    /**
     * 移除单道错题
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 操作结果
     */
    Result<Void> removeWrongQuestion(Long userId, Long questionId);

    /**
     * 批量移除错题
     *
     * @param userId      用户ID
     * @param questionIds 题目ID列表
     * @return 操作结果
     */
    Result<Void> batchRemoveWrongQuestions(Long userId, List<Long> questionIds);

    /**
     * 清空用户所有错题
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> clearAllWrongQuestions(Long userId);

    /**
     * 获取用户错题数量
     *
     * @param userId 用户ID
     * @return 错题数量
     */
    Result<Integer> getWrongCount(Long userId);
}