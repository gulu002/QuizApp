package com.quizapp.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 结束练习请求DTO
 * <p>用于结束练习会话接口</p>
 *
 * @author quizapp
 */
@Data
public class FinishSessionDTO {

    /** 练习会话ID，不能为空 */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
}