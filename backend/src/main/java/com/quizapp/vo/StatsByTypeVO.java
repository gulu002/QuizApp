package com.quizapp.vo;

import lombok.Data;

/**
 * 按题型统计VO
 * <p>用于展示按题目类型分组的统计数据</p>
 *
 * @author quizapp
 */
@Data
public class StatsByTypeVO {

    /** 题目类型：SINGLE-单选，MULTI-多选，JUDGE-判断 */
    private String questionType;

    /** 该题型总题数 */
    private Integer totalCount;

    /** 该题型答对题数 */
    private Integer correctCount;

    /** 该题型正确率（百分比） */
    private Double correctRate;
}