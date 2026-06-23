package com.quizapp.service.impl;

import com.quizapp.common.Result;
import com.quizapp.entity.AnswerRecord;
import com.quizapp.entity.Question;
import com.quizapp.entity.UserStats;
import com.quizapp.repository.AnswerRecordRepository;
import com.quizapp.repository.CategoryRepository;
import com.quizapp.repository.DailyCheckinRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.UserStatsRepository;
import com.quizapp.repository.WrongQuestionRepository;
import com.quizapp.repository.FavoriteRepository;
import com.quizapp.service.StatsService;
import com.quizapp.vo.StatsByTypeVO;
import com.quizapp.vo.UserStatsVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StatsServiceImpl implements StatsService {

    private final UserStatsRepository userStatsRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final DailyCheckinRepository dailyCheckinRepository;
    private final WrongQuestionRepository wrongQuestionRepository;
    private final FavoriteRepository favoriteRepository;

    public StatsServiceImpl(UserStatsRepository userStatsRepository,
                            AnswerRecordRepository answerRecordRepository,
                            QuestionRepository questionRepository,
                            CategoryRepository categoryRepository,
                            DailyCheckinRepository dailyCheckinRepository,
                            WrongQuestionRepository wrongQuestionRepository,
                            FavoriteRepository favoriteRepository) {
        this.userStatsRepository = userStatsRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.dailyCheckinRepository = dailyCheckinRepository;
        this.wrongQuestionRepository = wrongQuestionRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Result<UserStatsVO> getUserStats(Long userId) {
        Optional<UserStats> statsOpt = userStatsRepository.findByUserId(userId);
        UserStatsVO vo = new UserStatsVO();
        if (statsOpt.isPresent()) {
            UserStats stats = statsOpt.get();
            vo.setTotalAnswers(stats.getTotalAnswers());
            vo.setTotalCorrect(stats.getTotalCorrect());
            vo.setTotalWrong(stats.getTotalWrong());
            vo.setTotalDurationSec(stats.getTotalDurationSec());
            vo.setStreakDays(stats.getStreakDays());
            vo.setLastPracticeDate(stats.getLastPracticeDate());
            vo.setWrongBookCount(stats.getWrongBookCount());
            vo.setFavoriteCount(stats.getFavoriteCount());
            // correctRate is computed by UserStatsVO.getCorrectRate()
        } else {
            vo.setTotalAnswers(0);
            vo.setTotalCorrect(0);
            vo.setTotalWrong(0);
            // correctRate defaults to 0.0 by UserStatsVO.getCorrectRate()
            vo.setTotalDurationSec(0L);
            vo.setStreakDays(0);
            vo.setWrongBookCount(0);
            vo.setFavoriteCount(0);
        }
        return Result.success(vo);
    }

    @Override
    public Result<List<StatsByTypeVO>> getStatsByType(Long userId) {
        List<AnswerRecord> records = answerRecordRepository.findByUserIdAndCreateTimeBetween(
                userId, LocalDateTime.now().minusYears(10), LocalDateTime.now());
        Map<Long, String> questionTypeMap = new HashMap<>();
        Map<String, StatsByTypeVO> typeStats = new HashMap<>();

        for (AnswerRecord record : records) {
            String questionType = questionTypeMap.computeIfAbsent(record.getQuestionId(), qid -> {
                Optional<Question> qOpt = questionRepository.findById(qid);
                return qOpt.map(Question::getQuestionType).orElse("UNKNOWN");
            });

            StatsByTypeVO vo = typeStats.computeIfAbsent(questionType, k -> {
                StatsByTypeVO v = new StatsByTypeVO();
                v.setQuestionType(k);
                v.setTotalCount(0);
                v.setCorrectCount(0);
                v.setCorrectRate(0.0);
                return v;
            });
            vo.setTotalCount(vo.getTotalCount() + 1);
            if (record.getIsCorrect() == 1) {
                vo.setCorrectCount(vo.getCorrectCount() + 1);
            }
        }

        for (StatsByTypeVO vo : typeStats.values()) {
            if (vo.getTotalCount() > 0) {
                double rate = (double) vo.getCorrectCount() / vo.getTotalCount() * 100;
                vo.setCorrectRate(Math.round(rate * 10.0) / 10.0);
            }
        }

        return Result.success(new ArrayList<>(typeStats.values()));
    }

    @Override
    public Result<List<StatsByTypeVO>> getStatsByCategory(Long userId) {
        List<AnswerRecord> records = answerRecordRepository.findByUserIdAndCreateTimeBetween(
                userId, LocalDateTime.now().minusYears(10), LocalDateTime.now());
        Map<Long, String> categoryMap = new HashMap<>();
        Map<String, StatsByTypeVO> catStats = new HashMap<>();

        for (AnswerRecord record : records) {
            String categoryName = categoryMap.computeIfAbsent(record.getQuestionId(), qid -> {
                Optional<Question> qOpt = questionRepository.findById(qid);
                if (qOpt.isPresent()) {
                    return categoryRepository.findById(qOpt.get().getCategoryId())
                            .map(c -> c.getName())
                            .orElse("未知分类");
                }
                return "未知分类";
            });

            StatsByTypeVO vo = catStats.computeIfAbsent(categoryName, k -> {
                StatsByTypeVO v = new StatsByTypeVO();
                v.setQuestionType(k);
                v.setTotalCount(0);
                v.setCorrectCount(0);
                v.setCorrectRate(0.0);
                return v;
            });
            vo.setTotalCount(vo.getTotalCount() + 1);
            if (record.getIsCorrect() == 1) {
                vo.setCorrectCount(vo.getCorrectCount() + 1);
            }
        }

        for (StatsByTypeVO vo : catStats.values()) {
            if (vo.getTotalCount() > 0) {
                double rate = (double) vo.getCorrectCount() / vo.getTotalCount() * 100;
                vo.setCorrectRate(Math.round(rate * 10.0) / 10.0);
            }
        }

        return Result.success(new ArrayList<>(catStats.values()));
    }

    @Override
    public Result<Map<String, Object>> getStudyCalendar(Long userId, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Object[]> dailyCounts = dailyCheckinRepository.countByDateRange(userId, startDate, endDate);
        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("month", month);
        List<Map<String, Object>> days = new ArrayList<>();
        for (Object[] row : dailyCounts) {
            Map<String, Object> day = new HashMap<>();
            day.put("date", row[0].toString());
            day.put("count", row[1]);
            days.add(day);
        }
        result.put("days", days);
        return Result.success(result);
    }

    @Override
    public Result<List<Map<String, Object>>> getWeaknessAnalysis(Long userId) {
        List<AnswerRecord> records = answerRecordRepository.findByUserIdAndCreateTimeBetween(
                userId, LocalDateTime.now().minusYears(10), LocalDateTime.now());
        Map<Long, String> categoryMap = new HashMap<>();
        Map<String, int[]> catStats = new HashMap<>(); // {total, correct}

        for (AnswerRecord record : records) {
            String categoryName = categoryMap.computeIfAbsent(record.getQuestionId(), qid -> {
                Optional<Question> qOpt = questionRepository.findById(qid);
                if (qOpt.isPresent()) {
                    return categoryRepository.findById(qOpt.get().getCategoryId())
                            .map(c -> c.getName())
                            .orElse("未知分类");
                }
                return "未知分类";
            });

            int[] stats = catStats.computeIfAbsent(categoryName, k -> new int[]{0, 0});
            stats[0]++;
            if (record.getIsCorrect() == 1) {
                stats[1]++;
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : catStats.entrySet()) {
            int total = entry.getValue()[0];
            int correct = entry.getValue()[1];
            double rate = (double) correct / total * 100;
            if (rate < 60.0) {
                Map<String, Object> item = new HashMap<>();
                item.put("categoryName", entry.getKey());
                item.put("totalCount", total);
                item.put("correctCount", correct);
                item.put("correctRate", Math.round(rate * 10.0) / 10.0);
                result.add(item);
            }
        }

        return Result.success(result);
    }
}