package com.quizapp.entity;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 答案导入实体类
 * <p>对应数据库表 answer_import，存储从Excel导入的答案数据</p>
 *
 * @author quizapp
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "answer_import")
public class AnswerImport {

    /** 导入记录唯一标识，自增主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 来源题号，对应题目表的 source_number */
    @Column(name = "source_number")
    private Integer sourceNumber;

    /** 题目类型：SINGLE-单选，MULTI-多选，JUDGE-判断 */
    @Column(name = "question_type", length = 16)
    private String questionType;

    /** 正确答案 */
    @Column(name = "correct_answer", length = 32)
    private String correctAnswer;

    /** 题目解析，TEXT类型 */
    @Lob
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    /** 导入状态：0-未导入，1-已导入 */
    @Column(name = "imported")
    private Integer imported;

    /** 记录创建时间，由 Hibernate 自动填充，不可更新 */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
}