package com.quizapp.dto;

import lombok.Data;

/**
 * 题目查询DTO
 * <p>用于题目列表查询，支持关键词搜索、题型筛选、分类筛选和分页</p>
 *
 * @author quizapp
 */
@Data
public class QuestionQueryDTO {

    /** 搜索关键词，模糊匹配题目标题 */
    private String keyword;

    /** 题目类型筛选：SINGLE-单选，MULTI-多选，JUDGE-判断 */
    private String questionType;

    /** 分类ID筛选 */
    private Long categoryId;

    /** 当前页码，默认1 */
    private Integer page = 1;

    /** 每页大小，默认20 */
    private Integer size = 20;
}