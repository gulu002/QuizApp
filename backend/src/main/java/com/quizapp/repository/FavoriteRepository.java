package com.quizapp.repository;

import com.quizapp.entity.Favorite;
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
 * 收藏记录数据访问接口
 * <p>提供收藏记录表的基本CRUD操作及业务查询方法</p>
 *
 * @author quizapp
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    /**
     * 分页查询用户的收藏题目
     *
     * @param userId   用户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    /**
     * 查找用户对指定题目的收藏记录
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 收藏记录，不存在则返回空
     */
    Optional<Favorite> findByUserIdAndQuestionId(Long userId, Long questionId);

    /**
     * 统计用户的收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    int countByUserId(Long userId);

    /**
     * 批量删除用户的收藏记录
     *
     * @param userId      用户ID
     * @param questionIds 题目ID列表
     */
    @Modifying
    @Query("DELETE FROM Favorite f WHERE f.userId = :userId AND f.questionId IN :questionIds")
    void deleteByUserIdAndQuestionIds(@Param("userId") Long userId, @Param("questionIds") List<Long> questionIds);

    /**
     * 判断用户是否已收藏指定题目
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return true表示已收藏
     */
    boolean existsByUserIdAndQuestionId(Long userId, Long questionId);
}