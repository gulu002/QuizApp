package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体类
 * <p>对应数据库表 users，存储用户基本信息、微信绑定、状态等数据</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    /** 用户唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 手机号，用于登录认证，最大长度16 */
    @Column(name = "phone", length = 16)
    private String phone;

    /** 密码哈希值，使用 BCrypt 加密存储，最大长度128 */
    @Column(name = "password_hash", length = 128)
    private String passwordHash;

    /** 微信 OpenID，用于微信登录绑定，最大长度64 */
    @Column(name = "wechat_openid", length = 64)
    private String wechatOpenid;

    /** 微信 UnionID，用于跨平台用户识别，最大长度64 */
    @Column(name = "wechat_unionid", length = 64)
    private String wechatUnionid;

    /** 用户昵称，默认值"刷题人"，最大长度64 */
    @Column(name = "nickname", length = 64)
    private String nickname = "刷题人";

    /** 用户头像URL地址，最大长度512 */
    @Column(name = "avatar_url", length = 512)
    private String avatarUrl;

    /** 用户状态：1-正常，0-禁用 */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /** 最后登录时间 */
    @Column(name = "last_login_time")
    private LocalDateTime lastLoginTime;

    /** 记录创建时间，由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /** 记录最后更新时间，由 Hibernate 自动填充 */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}