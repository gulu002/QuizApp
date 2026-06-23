package com.quizapp.controller;

import com.quizapp.common.Result;
import com.quizapp.dto.QuestionQueryDTO;
import com.quizapp.service.QuestionService;
import org.springframework.web.bind.annotation.*;

/**
 * 题目控制器
 * <p>处理题目查询、详情、统计等请求</p>
 *
 * @author quizapp
 */
@RestController
@RequestMapping("/api/questions")
public class QuestionController extends BaseController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public Result<?> queryQuestions(QuestionQueryDTO queryDTO) {
        return questionService.queryQuestions(queryDTO);
    }

    @GetMapping("/{id}")
    public Result<?> getQuestionDetail(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        return questionService.getQuestionDetail(id, userId);
    }

    @GetMapping("/count")
    public Result<?> countByType(@RequestParam(required = false) String questionType) {
        return questionService.countByType(questionType);
    }

    @GetMapping("/categories/stats")
    public Result<?> getCategoryStats() {
        return questionService.getCategoryStats();
    }
}