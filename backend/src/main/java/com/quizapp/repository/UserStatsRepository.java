package com.quizapp.repository;

import com.quizapp.entity.UserStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户统计数据访问接口
 * <p>提供用户统计表的基本CRUD操作及业务查询方法</p>
 *
 * @author quizapp
 */
@Repository
public interface UserStatsRepository extends JpaRepository<UserStats, Long> {

    /**
     * 根据用户ID查找统计数据
     *
     * @param userId 用户ID
     * @return 统计数据，不存在则返回空
     */
    Optional<UserStats> findByUserId(Long userId);
}