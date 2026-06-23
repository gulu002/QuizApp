package com.quizapp.controller;

import com.quizapp.common.Result;
import com.quizapp.dto.FinishSessionDTO;
import com.quizapp.dto.PracticeStartDTO;
import com.quizapp.dto.SubmitAnswerDTO;
import com.quizapp.service.PracticeService;
import org.springframework.web.bind.annotation.*;

/**
 * 练习控制器
 * <p>处理开始练习、提交答案、结束练习、查看进度和历史等请求</p>
 *
 * @author quizapp
 */
@RestController
@RequestMapping("/api/practice")
public class PracticeController extends BaseController {

    private final PracticeService practiceService;

    public PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @PostMapping("/start")
    public Result<?> startPractice(@RequestBody PracticeStartDTO startDTO) {
        Long userId = getCurrentUserId();
        return practiceService.startPractice(userId, startDTO);
    }

    @PostMapping("/submit")
    public Result<?> submitAnswer(@RequestBody SubmitAnswerDTO submitDTO) {
        Long userId = getCurrentUserId();
        return practiceService.submitAnswer(userId, submitDTO);
    }

    @PostMapping("/finish")
    public Result<?> finishPractice(@RequestBody FinishSessionDTO finishDTO) {
        Long userId = getCurrentUserId();
        return practiceService.finishPractice(userId, finishDTO);
    }

    @GetMapping("/session/{sessionId}")
    public Result<?> getSessionProgress(@PathVariable String sessionId) {
        Long userId = getCurrentUserId();
        return practiceService.getSessionProgress(userId, sessionId);
    }

    @GetMapping("/session/{sessionId}/questions")
    public Result<?> getSessionQuestions(@PathVariable String sessionId) {
        Long userId = getCurrentUserId();
        return practiceService.getSessionQuestions(userId, sessionId);
    }

    @GetMapping("/history")
    public Result<?> getSessionHistory(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        return practiceService.getSessionHistory(userId, page, size);
    }
}