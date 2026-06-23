package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 答题记录实体类
 * <p>对应数据库表 answer_record，存储用户每次答题的详细记录</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answer_record")
public class AnswerRecord {

    /** 答题记录唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 答题用户ID，关联 user 表 */
    @Column(name = "user_id")
    private Long userId;

    /** 题目ID，关联 question 表 */
    @Column(name = "question_id")
    private Long questionId;

    /** 练习会话ID，关联 practice_session 表 */
    @Column(name = "session_id", length = 64)
    private String sessionId;

    /** 用户提交的答案 */
    @Column(name = "user_answer", length = 32)
    private String userAnswer;

    /** 是否正确：1-正确，0-错误 */
    @Column(name = "is_correct")
    private Integer isCorrect;

    /** 答题耗时（毫秒） */
    @Column(name = "duration_ms")
    private Integer durationMs;

    /** 记录创建时间，由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
}