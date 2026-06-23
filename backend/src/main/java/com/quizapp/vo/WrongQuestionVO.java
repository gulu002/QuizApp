package com.quizapp.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错题VO
 * <p>用于展示用户错题本中的题目信息</p>
 *
 * @author quizapp
 */
@Data
public class WrongQuestionVO {

    /** 错题记录ID */
    private Long id;

    /** 题目ID */
    private Long questionId;

    /** 题目标题 */
    private String title;

    /** 题目类型：SINGLE-单选，MULTI-多选，JUDGE-判断 */
    private String questionType;

    /** 做错次数 */
    private Integer wrongCount;

    /** 连续答对次数 */
    private Integer consecutiveRight;

    /** 最近做错时间 */
    private LocalDateTime lastWrongTime;
}