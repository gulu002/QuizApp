package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 题目实体类
 * <p>对应数据库表 question，存储题目信息，包括单选题、多选题、判断题等</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "question")
public class Question {

    /** 题目唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 所属分类ID，关联 category 表 */
    @Column(name = "category_id")
    private Long categoryId;

    /** 题目类型：SINGLE-单选，MULTI-多选，JUDGE-判断 */
    @Column(name = "question_type", nullable = false, length = 16)
    private String questionType;

    /** 题目内容，TEXT类型，支持长文本 */
    @Lob
    @Column(name = "title", nullable = false, columnDefinition = "TEXT")
    private String title;

    /** 选项JSON字符串，存储选项列表，JSON格式 */
    @Column(name = "options_json", nullable = false, columnDefinition = "json")
    private String optionsJson;

    /** 正确答案，单选题为选项字母，多选题为字母组合，判断题为"正确"或"错误" */
    @Column(name = "correct_answer", nullable = false, length = 32)
    private String correctAnswer = "";

    /** 题目解析，TEXT类型，支持长文本 */
    @Lob
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    /** 难度等级：1-简单，2-中等，3-困难 */
    @Column(name = "difficulty")
    private Integer difficulty = 1;

    /** 来源题号，用于标识题目来源 */
    @Column(name = "source_number")
    private Integer sourceNumber;

    /** 题目状态：1-启用，0-禁用 */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /** 记录创建时间，由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /** 记录最后更新时间，由 Hibernate 自动填充 */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}