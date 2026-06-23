package com.quizapp.service;

import com.quizapp.common.Result;
import com.quizapp.dto.QuestionQueryDTO;
import com.quizapp.entity.Category;
import com.quizapp.entity.Question;
import com.quizapp.entity.User;
import com.quizapp.vo.PageVO;

import java.util.List;
import java.util.Map;

/**
 * 管理员服务接口
 * <p>提供后台管理功能，包括题目管理、用户管理、分类管理、数据统计等</p>
 *
 * @author quizapp
 */
public interface AdminService {

    /**
     * 管理员查询题目列表（支持分页和筛选）
     *
     * @param dto 查询条件（关键词、题型、分类、分页参数）
     * @return 分页题目列表
     */
    Result<PageVO<Question>> adminQueryQuestions(QuestionQueryDTO dto);

    /**
     * 管理员创建题目
     *
     * @param question 题目信息
     * @return 创建的题目
     */
    Result<Question> adminCreateQuestion(Question question);

    /**
     * 管理员更新题目
     *
     * @param id       题目ID
     * @param question 题目信息
     * @return 更新后的题目
     */
    Result<Question> adminUpdateQuestion(Long id, Question question);

    /**
     * 管理员删除题目
     *
     * @param id 题目ID
     * @return 操作结果
     */
    Result<Void> adminDeleteQuestion(Long id);

    /**
     * 管理员切换题目状态（启用/禁用）
     *
     * @param id     题目ID
     * @param status 状态（1-启用，0-禁用）
     * @return 操作结果
     */
    Result<Void> adminToggleQuestionStatus(Long id, Integer status);

    /**
     * 管理员查询用户列表（分页）
     *
     * @param page 页码
     * @param size 每页大小
     * @return 分页用户列表
     */
    Result<PageVO<User>> adminQueryUsers(int page, int size);

    /**
     * 管理员切换用户状态（正常/禁用）
     *
     * @param userId 用户ID
     * @param status 状态（1-正常，0-禁用）
     * @return 操作结果
     */
    Result<Void> adminToggleUserStatus(Long userId, Integer status);

    /**
     * 获取所有分类列表
     *
     * @return 分类列表
     */
    Result<List<Category>> adminGetCategories();

    /**
     * 管理员创建分类
     *
     * @param category 分类信息
     * @return 创建的分类
     */
    Result<Category> adminCreateCategory(Category category);

    /**
     * 管理员更新分类
     *
     * @param id       分类ID
     * @param category 分类信息
     * @return 更新后的分类
     */
    Result<Category> adminUpdateCategory(Long id, Category category);

    /**
     * 管理员删除分类
     *
     * @param id 分类ID
     * @return 操作结果
     */
    Result<Void> adminDeleteCategory(Long id);

    /**
     * 获取仪表盘统计数据
     *
     * @return 统计数据（用户数、题目数、答题数等）
     */
    Result<Map<String, Object>> adminGetDashboardStats();

    /**
     * 同步答案数据到题目表
     *
     * @return 操作结果
     */
    Result<Void> adminSyncAnswers();
}