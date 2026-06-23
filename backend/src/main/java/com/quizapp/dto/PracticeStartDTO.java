package com.quizapp.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 开始练习请求DTO
 * <p>用于开始练习接口，包含练习类型、题目类型、分类等信息</p>
 *
 * @author quizapp
 */
@Data
public class PracticeStartDTO {

    /** 练习类型：ORDER-顺序练习，RANDOM-随机练习，EXAM-模拟考试，TYPE-题型专练，WRONG-错题复习，FAV-收藏复习 */
    @NotBlank(message = "练习类型不能为空")
    private String sessionType;

    /** 题目类型，题型专练时使用：SINGLE-单选，MULTI-多选，JUDGE-判断 */
    private String questionType;

    /** 分类ID，分类专练时使用 */
    private Long categoryId;

    /** 题目数量，默认20 */
    private Integer questionCount;

    /** 时间限制（分钟），模拟考试时使用 */
    private Integer timeLimitMin;
}