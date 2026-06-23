package com.quizapp.repository;

import com.quizapp.entity.WrongQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 错题本数据访问接口
 * <p>提供错题记录表的基本CRUD操作及业务查询方法</p>
 *
 * @author quizapp
 */
@Repository
public interface WrongQuestionRepository extends JpaRepository<WrongQuestion, Long> {

    /**
     * 分页查询用户指定状态的错题
     *
     * @param userId   用户ID
     * @param status   错题状态（1-未掌握，0-已掌握）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<WrongQuestion> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    /**
     * 查找用户对指定题目的错题记录
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 错题记录，不存在则返回空
     */
    Optional<WrongQuestion> findByUserIdAndQuestionId(Long userId, Long questionId);

    /**
     * 统计用户指定状态的错题数量
     *
     * @param userId 用户ID
     * @param status 错题状态
     * @return 错题数量
     */
    int countByUserIdAndStatus(Long userId, Integer status);

    /**
     * 批量删除用户的错题记录
     *
     * @param userId      用户ID
     * @param questionIds 题目ID列表
     */
    @Modifying
    @Query("DELETE FROM WrongQuestion w WHERE w.userId = :userId AND w.questionId IN :questionIds")
    void deleteByUserIdAndQuestionIds(@Param("userId") Long userId, @Param("questionIds") List<Long> questionIds);
}