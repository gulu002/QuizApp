package com.quizapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交答案请求DTO
 * <p>用于提交答案接口，包含会话ID、题目ID、用户答案等信息</p>
 *
 * @author quizapp
 */
@Data
public class SubmitAnswerDTO {

    /** 练习会话ID，不能为空 */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /** 题目ID，不能为空 */
    @NotNull(message = "题目ID不能为空")
    private Long questionId;

    /** 用户提交的答案，不能为空 */
    @NotBlank(message = "用户答案不能为空")
    private String userAnswer;

    /** 答题耗时（毫秒），可选 */
    private Integer durationMs;
}