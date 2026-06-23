package com.quizapp.vo;

import lombok.Data;

import java.util.List;

/**
 * 题目详情VO
 * <p>用于展示题目的完整信息，包括选项、答案、解析等</p>
 *
 * @author quizapp
 */
@Data
public class QuestionVO {

    /** 题目ID */
    private Long id;

    /** 分类ID */
    private Long categoryId;

    /** 分类名称 */
    private String categoryName;

    /** 题目类型：SINGLE-单选，MULTI-多选，JUDGE-判断 */
    private String questionType;

    /** 题目标题 */
    private String title;

    /** 选项列表 */
    private List<OptionVO> options;

    /** 正确答案 */
    private String correctAnswer;

    /** 题目解析 */
    private String explanation;

    /** 难度等级：1-简单，2-中等，3-困难 */
    private Integer difficulty;

    /** 来源题号 */
    private Integer sourceNumber;

    /** 是否已收藏 */
    private Boolean isFavorited;

    /** 是否在错题本中 */
    private Boolean isWrong;
}