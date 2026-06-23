package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 错题本实体类
 * <p>对应数据库表 wrong_question，存储用户做错的题目及复习状态</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wrong_question")
public class WrongQuestion {

    /** 错题记录唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID，关联 user 表 */
    @Column(name = "user_id")
    private Long userId;

    /** 题目ID，关联 question 表 */
    @Column(name = "question_id")
    private Long questionId;

    /** 做错次数 */
    @Column(name = "wrong_count")
    private Integer wrongCount;

    /** 连续答对次数，用于判断是否掌握 */
    @Column(name = "consecutive_right")
    private Integer consecutiveRight;

    /** 首次做错时间 */
    @Column(name = "first_wrong_time")
    private LocalDateTime firstWrongTime;

    /** 最近做错时间 */
    @Column(name = "last_wrong_time")
    private LocalDateTime lastWrongTime;

    /** 最近答题时间 */
    @Column(name = "last_answer_time")
    private LocalDateTime lastAnswerTime;

    /** 错题状态：1-未掌握，0-已掌握（连续答对3次） */
    @Column(name = "status")
    private Integer status;

    /** 记录创建时间，由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /** 记录最后更新时间，由 Hibernate 自动填充 */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}