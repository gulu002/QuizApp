package com.quizapp.repository;

import com.quizapp.entity.QuestionMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 题目掌握度数据访问接口
 * <p>提供题目掌握度记录的基本CRUD操作及业务查询方法</p>
 *
 * @author quizapp
 */
@Repository
public interface QuestionMasteryRepository extends JpaRepository<QuestionMastery, Long> {

    /**
     * 查找用户对指定题目的掌握度记录
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 掌握度记录，不存在则返回空
     */
    Optional<QuestionMastery> findByUserIdAndQuestionId(Long userId, Long questionId);

    /**
     * 查询用户指定掌握状态的题目列表
     *
     * @param userId   用户ID
     * @param mastered 掌握状态（1-已掌握，0-未掌握）
     * @return 掌握度记录列表
     */
    List<QuestionMastery> findByUserIdAndMastered(Long userId, Integer mastered);

    /**
     * 统计用户指定掌握状态的题目数量
     *
     * @param userId   用户ID
     * @param mastered 掌握状态
     * @return 题目数量
     */
    int countByUserIdAndMastered(Long userId, Integer mastered);
}