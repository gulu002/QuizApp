package com.quizapp.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求DTO
 * <p>用于用户登录接口，包含手机号和密码</p>
 *
 * @author quizapp
 */
@Data
public class LoginDTO {

    /** 手机号，不能为空 */
    @NotBlank(message = "手机号不能为空")
    private String phone;

    /** 密码，不能为空 */
    @NotBlank(message = "密码不能为空")
    private String password;
}