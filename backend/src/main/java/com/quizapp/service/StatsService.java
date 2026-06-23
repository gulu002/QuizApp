package com.quizapp.service;

import com.quizapp.common.Result;
import com.quizapp.vo.StatsByTypeVO;
import com.quizapp.vo.UserStatsVO;

import java.util.List;
import java.util.Map;

/**
 * 统计服务接口
 * <p>提供用户统计数据、题型统计、分类统计、学习日历、薄弱点分析等业务操作</p>
 *
 * @author quizapp
 */
public interface StatsService {

    /**
     * 获取用户整体统计数据
     *
     * @param userId 用户ID
     * @return 用户统计概览
     */
    Result<UserStatsVO> getUserStats(Long userId);

    /**
     * 按题型统计答题数据
     *
     * @param userId 用户ID
     * @return 各题型统计列表
     */
    Result<List<StatsByTypeVO>> getStatsByType(Long userId);

    /**
     * 按分类统计答题数据
     *
     * @param userId 用户ID
     * @return 各分类统计列表
     */
    Result<List<StatsByTypeVO>> getStatsByCategory(Long userId);

    /**
     * 获取学习日历数据
     *
     * @param userId 用户ID
     * @param year   年份
     * @param month  月份
     * @return 日历数据（日期与答题数量映射）
     */
    Result<Map<String, Object>> getStudyCalendar(Long userId, int year, int month);

    /**
     * 获取薄弱点分析数据
     *
     * @param userId 用户ID
     * @return 薄弱点分析列表（按正确率排序）
     */
    Result<List<Map<String, Object>>> getWeaknessAnalysis(Long userId);
}