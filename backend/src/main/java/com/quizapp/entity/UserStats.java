package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户统计实体类
 * <p>对应数据库表 user_stats，存储用户练习统计数据</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_stats")
public class UserStats {

    /** 统计记录唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID，关联 user 表 */
    @Column(name = "user_id")
    private Long userId;

    /** 总答题数 */
    @Column(name = "total_answers")
    private Integer totalAnswers;

    /** 总答对数 */
    @Column(name = "total_correct")
    private Integer totalCorrect;

    /** 总答错数 */
    @Column(name = "total_wrong")
    private Integer totalWrong;

    /** 错题本数量 */
    @Column(name = "wrong_book_count")
    private Integer wrongBookCount;

    /** 收藏数量 */
    @Column(name = "favorite_count")
    private Integer favoriteCount;

    /** 总练习时长（秒） */
    @Column(name = "total_duration_sec")
    private Long totalDurationSec;

    /** 连续打卡天数 */
    @Column(name = "streak_days")
    private Integer streakDays;

    /** 最近练习日期 */
    @Column(name = "last_practice_date")
    private LocalDate lastPracticeDate;

    /** 记录更新时间 */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}