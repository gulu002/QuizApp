package com.quizapp.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 收藏题目VO
 * <p>用于展示用户收藏的题目信息</p>
 *
 * @author quizapp
 */
@Data
public class FavoriteVO {

    /** 收藏记录ID */
    private Long id;

    /** 题目ID */
    private Long questionId;

    /** 题目标题 */
    private String title;

    /** 题目类型：SINGLE-单选，MULTI-多选，JUDGE-判断 */
    private String questionType;

    /** 收藏时间 */
    private LocalDateTime createTime;
}