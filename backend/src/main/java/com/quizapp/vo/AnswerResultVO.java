package com.quizapp.vo;

import lombok.Data;

/**
 * 答题结果VO
 * <p>用于展示提交答案后的结果反馈</p>
 *
 * @author quizapp
 */
@Data
public class AnswerResultVO {

    /** 是否答对 */
    private Boolean isCorrect;

    /** 正确答案 */
    private String correctAnswer;

    /** 题目解析 */
    private String explanation;

    /** 是否已掌握该题 */
    private Boolean isMastered;
}