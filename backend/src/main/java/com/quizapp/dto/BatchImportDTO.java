package com.quizapp.dto;

import lombok.Data;

/**
 * 批量导入题目DTO
 * <p>用于Excel批量导入题目数据</p>
 *
 * @author quizapp
 */
@Data
public class BatchImportDTO {
    /** 来源题号 */
    private Integer sourceNumber;
    /** 题目类型：SINGLE-单选，MULTI-多选，JUDGE-判断 */
    private String questionType;
    /** 正确答案 */
    private String correctAnswer;
    /** 题目解析 */
    private String explanation;
}