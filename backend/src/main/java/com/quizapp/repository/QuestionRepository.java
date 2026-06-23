package com.quizapp.repository;

import com.quizapp.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 题目数据访问接口
 * <p>提供题目表的基本CRUD操作、分页查询及随机抽题等高级查询方法</p>
 *
 * @author quizapp
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    /**
     * 按题型分页查询题目
     *
     * @param questionType 题目类型
     * @param pageable     分页参数
     * @return 分页结果
     */
    Page<Question> findByQuestionType(String questionType, Pageable pageable);

    /**
     * 按分类ID分页查询题目
     *
     * @param categoryId 分类ID
     * @param pageable   分页参数
     * @return 分页结果
     */
    Page<Question> findByCategoryId(Long categoryId, Pageable pageable);

    /**
     * 按题型和分类ID分页查询题目
     *
     * @param questionType 题目类型
     * @param categoryId   分类ID
     * @param pageable     分页参数
     * @return 分页结果
     */
    Page<Question> findByQuestionTypeAndCategoryId(String questionType, Long categoryId, Pageable pageable);

    /**
     * 按关键词模糊搜索题目标题
     *
     * @param keyword  搜索关键词
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Question> findByTitleContaining(String keyword, Pageable pageable);

    /**
     * 按状态分页查询题目
     *
     * @param status   题目状态
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<Question> findByStatus(Integer status, Pageable pageable);

    /**
     * 根据来源题号查找题目
     *
     * @param sourceNumber 来源题号
     * @return 题目信息，不存在则返回空
     */
    Optional<Question> findBySourceNumber(Integer sourceNumber);

    /**
     * 统计指定题型的题目数量
     *
     * @param questionType 题目类型
     * @return 题目数量
     */
    long countByQuestionType(String questionType);

    /**
     * 统计指定分类的题目数量
     *
     * @param categoryId 分类ID
     * @return 题目数量
     */
    long countByCategoryId(Long categoryId);

    /**
     * 随机抽取指定题型的题目
     *
     * @param questionType 题目类型
     * @param limit        抽取数量上限
     * @return 题目列表
     */
    @Query(value = "SELECT * FROM question WHERE status = 1 AND question_type = :questionType ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("questionType") String questionType, @Param("limit") int limit);

    /**
     * 随机抽取指定题型和分类的题目
     *
     * @param questionType 题目类型
     * @param categoryId   分类ID
     * @param limit        抽取数量上限
     * @return 题目列表
     */
    @Query(value = "SELECT * FROM question WHERE status = 1 AND question_type = :questionType AND category_id = :categoryId ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByCategory(@Param("questionType") String questionType, @Param("categoryId") Long categoryId, @Param("limit") int limit);

    /**
     * 随机抽取任意题型的题目
     *
     * @param limit 抽取数量上限
     * @return 题目列表
     */
    @Query(value = "SELECT * FROM question WHERE status = 1 ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);
}