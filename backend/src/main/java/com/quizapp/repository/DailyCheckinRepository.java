package com.quizapp.repository;

import com.quizapp.entity.DailyCheckin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 每日签到数据访问接口
 * <p>提供签到记录表的基本CRUD操作及统计查询方法</p>
 *
 * @author quizapp
 */
@Repository
public interface DailyCheckinRepository extends JpaRepository<DailyCheckin, Long> {

    /**
     * 查找用户指定日期的签到记录
     *
     * @param userId 用户ID
     * @param date   签到日期
     * @return 签到记录，不存在则返回空
     */
    Optional<DailyCheckin> findByUserIdAndCheckinDate(Long userId, LocalDate date);

    /**
     * 查询用户指定日期范围内的签到记录
     *
     * @param userId 用户ID
     * @param start  开始日期
     * @param end    结束日期
     * @return 签到记录列表
     */
    List<DailyCheckin> findByUserIdAndCheckinDateBetween(Long userId, LocalDate start, LocalDate end);

    /**
     * 判断用户指定日期是否已签到
     *
     * @param userId 用户ID
     * @param date   签到日期
     * @return true表示已签到
     */
    boolean existsByUserIdAndCheckinDate(Long userId, LocalDate date);

    /**
     * 按日期范围查询用户签到数据（用于学习日历）
     *
     * @param userId 用户ID
     * @param start  开始日期
     * @param end    结束日期
     * @return 日期与答题数量的数组列表
     */
    @Query("SELECT d.checkinDate, d.answerCount FROM DailyCheckin d WHERE d.userId = :userId AND d.checkinDate BETWEEN :start AND :end")
    List<Object[]> countByDateRange(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);
}