package com.quizapp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求DTO
 * <p>用于用户注册接口，包含手机号、密码和昵称</p>
 *
 * @author quizapp
 */
@Data
public class RegisterDTO {

    /** 手机号，不能为空，格式：1开头，第二位3-9，共11位 */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 密码，不能为空，长度6-20位 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度需在6-20位之间")
    private String password;

    /** 用户昵称，可选 */
    private String nickname;
}