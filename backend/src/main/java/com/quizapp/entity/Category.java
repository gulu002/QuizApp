package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 分类实体类
 * <p>对应数据库表 category，存储题目分类信息，支持多级分类</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category")
public class Category {

    /** 分类唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 分类名称，不能为空，最大长度64 */
    @Column(name = "name", nullable = false, length = 64)
    private String name;

    /** 父分类ID，0表示顶级分类 */
    @Column(name = "parent_id")
    private Long parentId = 0L;

    /** 排序序号，用于控制分类显示顺序 */
    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    /** 记录创建时间，由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /** 记录最后更新时间，由 Hibernate 自动填充 */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}