package com.quizapp.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页响应VO
 * <p>通用分页数据结构，封装分页查询结果</p>
 *
 * @param <T> 数据类型
 * @author quizapp
 */
@Data
public class PageVO<T> {

    /** 当前页数据列表 */
    private List<T> content;

    /** 总记录数 */
    private Long totalElements;

    /** 总页数 */
    private Integer totalPages;

    /** 当前页码 */
    private Integer currentPage;

    /** 每页大小 */
    private Integer size;
}