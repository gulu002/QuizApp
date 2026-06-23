package com.quizapp.controller;

import com.quizapp.common.Result;
import com.quizapp.dto.QuestionQueryDTO;
import com.quizapp.entity.Category;
import com.quizapp.entity.Question;
import com.quizapp.service.AdminService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员控制器
 * <p>处理后台管理功能，包括题目管理、用户管理、分类管理、数据统计等请求</p>
 *
 * @author quizapp
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController extends BaseController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/questions")
    public Result<?> listQuestions(QuestionQueryDTO queryDTO) {
        return adminService.adminQueryQuestions(queryDTO);
    }

    @PostMapping("/questions")
    public Result<?> createQuestion(@RequestBody Question question) {
        return adminService.adminCreateQuestion(question);
    }

    @PutMapping("/questions/{id}")
    public Result<?> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        return adminService.adminUpdateQuestion(id, question);
    }

    @DeleteMapping("/questions/{id}")
    public Result<?> deleteQuestion(@PathVariable Long id) {
        return adminService.adminDeleteQuestion(id);
    }

    @PutMapping("/questions/{id}/status")
    public Result<?> updateQuestionStatus(@PathVariable Long id, @RequestParam Integer status) {
        return adminService.adminToggleQuestionStatus(id, status);
    }

    @GetMapping("/users")
    public Result<?> listUsers(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size) {
        return adminService.adminQueryUsers(page, size);
    }

    @PutMapping("/users/{userId}/status")
    public Result<?> updateUserStatus(@PathVariable Long userId, @RequestParam Integer status) {
        return adminService.adminToggleUserStatus(userId, status);
    }

    @GetMapping("/categories")
    public Result<?> listCategories() {
        return adminService.adminGetCategories();
    }

    @PostMapping("/categories")
    public Result<?> createCategory(@RequestBody Category category) {
        return adminService.adminCreateCategory(category);
    }

    @PutMapping("/categories/{id}")
    public Result<?> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return adminService.adminUpdateCategory(id, category);
    }

    @DeleteMapping("/categories/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        return adminService.adminDeleteCategory(id);
    }

    @GetMapping("/dashboard")
    public Result<?> dashboard() {
        return adminService.adminGetDashboardStats();
    }

    @PostMapping("/sync-answers")
    public Result<?> syncAnswers() {
        return adminService.adminSyncAnswers();
    }
}