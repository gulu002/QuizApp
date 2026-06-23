package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 收藏记录实体类
 * <p>对应数据库表 favorite，存储用户收藏的题目</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "favorite")
public class Favorite {

    /** 收藏记录唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID，关联 user 表 */
    @Column(name = "user_id")
    private Long userId;

    /** 题目ID，关联 question 表 */
    @Column(name = "question_id")
    private Long questionId;

    /** 记录创建时间，由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
}