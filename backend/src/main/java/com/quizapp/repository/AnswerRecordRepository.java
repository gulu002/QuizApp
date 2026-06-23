package com.quizapp.repository;

import com.quizapp.entity.AnswerRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 答题记录数据访问接口
 * <p>提供答题记录表的基本CRUD操作及统计分析方法</p>
 *
 * @author quizapp
 */
@Repository
public interface AnswerRecordRepository extends JpaRepository<AnswerRecord, Long> {

    /**
     * 查询用户在指定会话中的所有答题记录
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 答题记录列表
     */
    List<AnswerRecord> findByUserIdAndSessionId(Long userId, String sessionId);

    /**
     * 查询用户在指定题目的所有答题记录
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 答题记录列表
     */
    List<AnswerRecord> findByUserIdAndQuestionId(Long userId, Long questionId);

    /**
     * 统计用户在指定会话中的答题总数
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @return 答题数量
     */
    int countByUserIdAndSessionId(Long userId, String sessionId);

    /**
     * 统计用户在指定会话中答对/答错的题目数
     *
     * @param userId    用户ID
     * @param sessionId 会话ID
     * @param isCorrect 是否正确（1-正确，0-错误）
     * @return 答题数量
     */
    int countByUserIdAndSessionIdAndIsCorrect(Long userId, String sessionId, Integer isCorrect);

    /**
     * 查询用户在指定时间范围内的答题记录
     *
     * @param userId 用户ID
     * @param start  开始时间
     * @param end    结束时间
     * @return 答题记录列表
     */
    List<AnswerRecord> findByUserIdAndCreateTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);

    /**
     * 统计用户练习的不同天数（用于计算打卡天数）
     *
     * @param userId 用户ID
     * @return 练习天数
     */
    @Query(value = "SELECT COUNT(DISTINCT CAST(a.create_time AS DATE)) FROM answer_record a WHERE a.user_id = :userId", nativeQuery = true)
    int countDistinctPracticeDays(@Param("userId") Long userId);

    /**
     * 按日期分组统计用户答题数量（用于学习日历）
     *
     * @param userId 用户ID
     * @param start  开始时间
     * @param end    结束时间
     * @return 日期与答题数量的数组列表
     */
    @Query(value = "SELECT CAST(a.create_time AS DATE) as date, COUNT(*) as cnt FROM answer_record a WHERE a.user_id = :userId AND a.create_time BETWEEN :start AND :end GROUP BY CAST(a.create_time AS DATE)", nativeQuery = true)
    List<Object[]> countByDateRange(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}