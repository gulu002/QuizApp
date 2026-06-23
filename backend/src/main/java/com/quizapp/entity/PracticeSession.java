package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 练习会话实体类
 * <p>对应数据库表 practice_session，存储用户练习会话信息</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "practice_session")
public class PracticeSession {

    /** 会话唯一标识，UUID格式，主键 */
    @Id
    @Column(name = "session_id", length = 64)
    private String sessionId;

    /** 用户ID，关联 user 表 */
    @Column(name = "user_id")
    private Long userId;

    /** 会话类型：ORDER-顺序练习，RANDOM-随机练习，EXAM-模拟考试，TYPE-题型专练，WRONG-错题复习，FAV-收藏复习 */
    @Column(name = "session_type", length = 16)
    private String sessionType;

    /** 题型专练时的题目类型，其他类型为空 */
    @Column(name = "question_type", length = 16)
    private String questionType;

    /** 分类ID，分类专练时的分类 */
    @Column(name = "category_id")
    private Long categoryId;

    /** 题目总数 */
    @Column(name = "total_count")
    private Integer totalCount;

    /** 答对题数 */
    @Column(name = "correct_count")
    private Integer correctCount;

    /** 开始时间 */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /** 结束时间 */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /** 练习时长（秒） */
    @Column(name = "duration_sec")
    private Integer durationSec;

    /** 会话状态：1-进行中，2-已结束 */
    @Column(name = "status")
    private Integer status;
}