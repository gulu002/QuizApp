package com.quizapp.controller;

import com.quizapp.common.Result;
import com.quizapp.service.StatsService;
import org.springframework.web.bind.annotation.*;

/**
 * 统计控制器
 * <p>处理用户统计数据、题型统计、分类统计、学习日历、薄弱点分析等请求</p>
 *
 * @author quizapp
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController extends BaseController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/overview")
    public Result<?> getUserStats() {
        Long userId = getCurrentUserId();
        return statsService.getUserStats(userId);
    }

    @GetMapping("/by-type")
    public Result<?> getStatsByType() {
        Long userId = getCurrentUserId();
        return statsService.getStatsByType(userId);
    }

    @GetMapping("/by-category")
    public Result<?> getStatsByCategory() {
        Long userId = getCurrentUserId();
        return statsService.getStatsByCategory(userId);
    }

    @GetMapping("/calendar")
    public Result<?> getStudyCalendar(@RequestParam int year, @RequestParam int month) {
        Long userId = getCurrentUserId();
        return statsService.getStudyCalendar(userId, year, month);
    }

    @GetMapping("/weakness")
    public Result<?> getWeaknessAnalysis() {
        Long userId = getCurrentUserId();
        return statsService.getWeaknessAnalysis(userId);
    }
}