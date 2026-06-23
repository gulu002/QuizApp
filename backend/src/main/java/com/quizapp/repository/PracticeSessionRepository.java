package com.quizapp.repository;

import com.quizapp.entity.PracticeSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 练习会话数据访问接口
 * <p>提供练习会话表的基本CRUD操作及业务查询方法</p>
 *
 * @author quizapp
 */
@Repository
public interface PracticeSessionRepository extends JpaRepository<PracticeSession, String> {

    /**
     * 根据会话ID和用户ID查找会话
     *
     * @param sessionId 会话ID
     * @param userId    用户ID
     * @return 会话信息，不存在则返回空
     */
    Optional<PracticeSession> findBySessionIdAndUserId(String sessionId, Long userId);

    /**
     * 分页查询用户指定状态的会话
     *
     * @param userId   用户ID
     * @param status   会话状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<PracticeSession> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    /**
     * 分页查询用户的所有会话，按开始时间倒序
     *
     * @param userId   用户ID
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<PracticeSession> findByUserIdOrderByStartTimeDesc(Long userId, Pageable pageable);
}