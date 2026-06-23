package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 题目掌握度实体类
 * <p>对应数据库表 question_mastery，存储用户对每道题的掌握情况</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question_mastery")
public class QuestionMastery {

    /** 掌握度记录唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID，关联 user 表 */
    @Column(name = "user_id")
    private Long userId;

    /** 题目ID，关联 question 表 */
    @Column(name = "question_id")
    private Long questionId;

    /** 总答题次数 */
    @Column(name = "total_attempts")
    private Integer totalAttempts;

    /** 答对次数 */
    @Column(name = "correct_attempts")
    private Integer correctAttempts;

    /** 连续答对次数，达到3次则掌握 */
    @Column(name = "consecutive_right")
    private Integer consecutiveRight;

    /** 是否已掌握：1-已掌握，0-未掌握 */
    @Column(name = "mastered")
    private Integer mastered;

    /** 最近答题时间 */
    @Column(name = "last_answer_time")
    private LocalDateTime lastAnswerTime;

    /** 记录更新时间 */
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}