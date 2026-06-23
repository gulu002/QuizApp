package com.quizapp.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 练习会话VO
 * <p>用于展示练习会话的详细信息</p>
 *
 * @author quizapp
 */
@Data
public class PracticeSessionVO {

    /** 会话ID */
    private String sessionId;

    /** 会话类型：ORDER-顺序练习，RANDOM-随机练习，EXAM-模拟考试，TYPE-题型专练 */
    private String sessionType;

    /** 题目类型（题型专练时使用） */
    private String questionType;

    /** 总题数 */
    private Integer totalCount;

    /** 答对题数 */
    private Integer correctCount;

    /** 已答题数 */
    private Integer answeredCount;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 练习时长（秒） */
    private Integer durationSec;

    /** 会话状态：1-进行中，2-已结束 */
    private Integer status;
}