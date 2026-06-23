package com.quizapp.vo;

import lombok.Data;

/**
 * 登录响应VO
 * <p>用于登录/注册成功后返回的用户信息和Token</p>
 *
 * @author quizapp
 */
@Data
public class LoginVO {

    /** JWT认证令牌 */
    private String token;

    /** 用户ID */
    private Long userId;

    /** 用户昵称 */
    private String nickname;

    /** 用户头像URL */
    private String avatarUrl;
}