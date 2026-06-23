package com.quizapp.vo;

import lombok.Data;

import java.time.LocalDate;

/**
 * 用户统计概览VO
 * <p>用于展示用户的整体练习统计数据</p>
 *
 * @author quizapp
 */
@Data
public class UserStatsVO {

    /** 总答题数 */
    private Integer totalAnswers;

    /** 总答对题数 */
    private Integer totalCorrect;

    /** 总答错题数 */
    private Integer totalWrong;

    /** 错题本题数 */
    private Integer wrongBookCount;

    /** 收藏题数 */
    private Integer favoriteCount;

    /** 总练习时长（秒） */
    private Long totalDurationSec;

    /** 连续打卡天数 */
    private Integer streakDays;

    /** 最近练习日期 */
    private LocalDate lastPracticeDate;

    /**
     * 计算正确率
     *
     * @return 正确率百分比，保留两位小数
     */
    public Double getCorrectRate() {
        if (totalAnswers == null || totalAnswers == 0) {
            return 0.0;
        }
        if (totalCorrect == null) {
            return 0.0;
        }
        return Math.round(totalCorrect * 10000.0 / totalAnswers) / 100.0;
    }
}