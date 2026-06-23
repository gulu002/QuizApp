package com.quizapp.controller;

import com.quizapp.common.Result;
import com.quizapp.service.WrongQuestionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 错题本控制器
 * <p>处理错题查询、移除、清空等请求</p>
 *
 * @author quizapp
 */
@RestController
@RequestMapping("/api/wrong-questions")
public class WrongQuestionController extends BaseController {

    private final WrongQuestionService wrongQuestionService;

    public WrongQuestionController(WrongQuestionService wrongQuestionService) {
        this.wrongQuestionService = wrongQuestionService;
    }

    @GetMapping
    public Result<?> getWrongQuestions(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        return wrongQuestionService.getWrongQuestions(userId, page, size);
    }

    @DeleteMapping("/{questionId}")
    public Result<?> removeWrongQuestion(@PathVariable Long questionId) {
        Long userId = getCurrentUserId();
        return wrongQuestionService.removeWrongQuestion(userId, questionId);
    }

    @DeleteMapping("/batch")
    public Result<?> batchRemoveWrongQuestions(@RequestBody List<Long> questionIds) {
        Long userId = getCurrentUserId();
        return wrongQuestionService.batchRemoveWrongQuestions(userId, questionIds);
    }

    @DeleteMapping("/clear")
    public Result<?> clearAllWrongQuestions() {
        Long userId = getCurrentUserId();
        return wrongQuestionService.clearAllWrongQuestions(userId);
    }

    @GetMapping("/count")
    public Result<?> getWrongCount() {
        Long userId = getCurrentUserId();
        return wrongQuestionService.getWrongCount(userId);
    }
}