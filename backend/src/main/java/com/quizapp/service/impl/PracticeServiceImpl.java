package com.quizapp.service.impl;

import com.quizapp.common.Result;
import com.quizapp.dto.FinishSessionDTO;
import com.quizapp.dto.PracticeStartDTO;
import com.quizapp.dto.SubmitAnswerDTO;
import com.quizapp.entity.AnswerRecord;
import com.quizapp.entity.DailyCheckin;
import com.quizapp.entity.PracticeSession;
import com.quizapp.entity.Question;
import com.quizapp.entity.QuestionMastery;
import com.quizapp.entity.UserStats;
import com.quizapp.entity.WrongQuestion;
import com.quizapp.repository.AnswerRecordRepository;
import com.quizapp.repository.DailyCheckinRepository;
import com.quizapp.repository.FavoriteRepository;
import com.quizapp.repository.PracticeSessionRepository;
import com.quizapp.repository.QuestionMasteryRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.UserStatsRepository;
import com.quizapp.repository.WrongQuestionRepository;
import com.quizapp.service.PracticeService;
import com.quizapp.vo.AnswerResultVO;
import com.quizapp.vo.PageVO;
import com.quizapp.vo.PracticeSessionVO;
import com.quizapp.vo.QuestionVO;
import com.quizapp.vo.OptionVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 练习服务实现类
 * <p>实现开始练习、提交答案、结束练习、查看进度和历史等业务逻辑</p>
 *
 * @author quizapp
 */
@Service
public class PracticeServiceImpl implements PracticeService {

    private final PracticeSessionRepository practiceSessionRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final WrongQuestionRepository wrongQuestionRepository;
    private final QuestionMasteryRepository questionMasteryRepository;
    private final DailyCheckinRepository dailyCheckinRepository;
    private final UserStatsRepository userStatsRepository;
    private final FavoriteRepository favoriteRepository;
    private final ObjectMapper objectMapper;

    public PracticeServiceImpl(PracticeSessionRepository practiceSessionRepository,
                               QuestionRepository questionRepository,
                               AnswerRecordRepository answerRecordRepository,
                               WrongQuestionRepository wrongQuestionRepository,
                               QuestionMasteryRepository questionMasteryRepository,
                               DailyCheckinRepository dailyCheckinRepository,
                               UserStatsRepository userStatsRepository,
                               FavoriteRepository favoriteRepository,
                               ObjectMapper objectMapper) {
        this.practiceSessionRepository = practiceSessionRepository;
        this.questionRepository = questionRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.wrongQuestionRepository = wrongQuestionRepository;
        this.questionMasteryRepository = questionMasteryRepository;
        this.dailyCheckinRepository = dailyCheckinRepository;
        this.userStatsRepository = userStatsRepository;
        this.favoriteRepository = favoriteRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Result<PracticeSessionVO> startPractice(Long userId, PracticeStartDTO dto) {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        List<Question> questions = getQuestionsBySessionType(dto, userId);

        PracticeSession session = new PracticeSession();
        session.setSessionId(sessionId);
        session.setUserId(userId);
        session.setSessionType(dto.getSessionType());
        session.setQuestionType(dto.getQuestionType());
        session.setCategoryId(dto.getCategoryId());
        session.setTotalCount(questions.size());
        session.setCorrectCount(0);
        session.setStartTime(LocalDateTime.now());
        session.setStatus(1);
        practiceSessionRepository.save(session);

        PracticeSessionVO vo = convertSessionToVO(session);
        return Result.success(vo);
    }

    @Override
    @Transactional
    public Result<AnswerResultVO> submitAnswer(Long userId, SubmitAnswerDTO dto) {
        Optional<PracticeSession> sessionOpt = practiceSessionRepository.findBySessionIdAndUserId(dto.getSessionId(), userId);
        if (!sessionOpt.isPresent()) {
            return Result.error("练习会话不存在");
        }

        Optional<Question> questionOpt = questionRepository.findById(dto.getQuestionId());
        if (!questionOpt.isPresent()) {
            return Result.error("题目不存在");
        }
        Question question = questionOpt.get();
        boolean isCorrect;
        if ("MULTI".equals(question.getQuestionType())) {
            // 多选题答案排序后比较，忽略顺序
            char[] userArr = dto.getUserAnswer().trim().toLowerCase().toCharArray();
            char[] correctArr = question.getCorrectAnswer().trim().toLowerCase().toCharArray();
            Arrays.sort(userArr);
            Arrays.sort(correctArr);
            isCorrect = Arrays.equals(userArr, correctArr);
        } else {
            // 单选题和判断题直接比较
            isCorrect = question.getCorrectAnswer() != null
                    && question.getCorrectAnswer().equalsIgnoreCase(dto.getUserAnswer().trim());
        }

        // Create AnswerRecord
        AnswerRecord record = new AnswerRecord();
        record.setUserId(userId);
        record.setQuestionId(dto.getQuestionId());
        record.setSessionId(dto.getSessionId());
        record.setUserAnswer(dto.getUserAnswer());
        record.setIsCorrect(isCorrect ? 1 : 0);
        record.setDurationMs(dto.getDurationMs());
        record.setCreateTime(LocalDateTime.now());
        answerRecordRepository.save(record);

        // Update WrongQuestion
        updateWrongQuestion(userId, dto.getQuestionId(), isCorrect);

        // Update QuestionMastery
        updateQuestionMastery(userId, dto.getQuestionId(), isCorrect);

        // Update DailyCheckin
        updateDailyCheckin(userId);

        // Update UserStats
        updateUserStats(userId, isCorrect, dto.getDurationMs() != null ? dto.getDurationMs().longValue() : null);

        // Check if mastered
        boolean mastered = questionMasteryRepository.findByUserIdAndQuestionId(userId, dto.getQuestionId())
                .map(m -> m.getMastered() == 1)
                .orElse(false);

        AnswerResultVO resultVO = new AnswerResultVO();
        resultVO.setIsCorrect(isCorrect);
        resultVO.setCorrectAnswer(question.getCorrectAnswer());
        resultVO.setExplanation(question.getExplanation());
        resultVO.setIsMastered(mastered);
        return Result.success(resultVO);
    }

    @Override
    @Transactional
    public Result<PracticeSessionVO> finishPractice(Long userId, FinishSessionDTO dto) {
        Optional<PracticeSession> sessionOpt = practiceSessionRepository.findBySessionIdAndUserId(dto.getSessionId(), userId);
        if (!sessionOpt.isPresent()) {
            return Result.error("练习会话不存在");
        }
        PracticeSession session = sessionOpt.get();
        session.setEndTime(LocalDateTime.now());
        if (session.getStartTime() != null) {
            session.setDurationSec((int) ChronoUnit.SECONDS.between(session.getStartTime(), session.getEndTime()));
        }
        session.setStatus(2);

        long correctCount = answerRecordRepository.countByUserIdAndSessionIdAndIsCorrect(userId, dto.getSessionId(), 1);
        session.setCorrectCount((int) correctCount);
        practiceSessionRepository.save(session);

        return Result.success(convertSessionToVO(session));
    }

    @Override
    public Result<PracticeSessionVO> getSessionProgress(Long userId, String sessionId) {
        Optional<PracticeSession> sessionOpt = practiceSessionRepository.findBySessionIdAndUserId(sessionId, userId);
        if (!sessionOpt.isPresent()) {
            return Result.error("练习会话不存在");
        }
        PracticeSession session = sessionOpt.get();
        long answeredCount = answerRecordRepository.countByUserIdAndSessionId(userId, sessionId);
        PracticeSessionVO vo = convertSessionToVO(session);
        vo.setAnsweredCount((int) answeredCount);
        return Result.success(vo);
    }

    @Override
    public Result<PageVO<PracticeSessionVO>> getSessionHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "startTime"));
        Page<PracticeSession> sessionPage = practiceSessionRepository.findByUserIdOrderByStartTimeDesc(userId, pageable);
        List<PracticeSessionVO> voList = sessionPage.getContent().stream()
                .map(this::convertSessionToVO)
                .collect(Collectors.toList());

        PageVO<PracticeSessionVO> pageVO = new PageVO<>();
        pageVO.setContent(voList);
        pageVO.setTotalElements(sessionPage.getTotalElements());
        pageVO.setTotalPages(sessionPage.getTotalPages());
        pageVO.setCurrentPage(page);
        pageVO.setSize(size);
        return Result.success(pageVO);
    }

    @Override
    public Result<List<QuestionVO>> getSessionQuestions(Long userId, String sessionId) {
        List<AnswerRecord> records = answerRecordRepository.findByUserIdAndSessionId(userId, sessionId);
        List<Long> questionIds = records.stream()
                .map(AnswerRecord::getQuestionId)
                .distinct()
                .collect(Collectors.toList());
        if (questionIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        List<Question> questions = questionRepository.findAllById(questionIds);
        List<QuestionVO> voList = questions.stream()
                .map(q -> convertQuestionToVO(q, userId))
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    private List<Question> getQuestionsBySessionType(PracticeStartDTO dto, Long userId) {
        String sessionType = dto.getSessionType();
        Integer questionCount = dto.getQuestionCount();
        if (questionCount == null || questionCount <= 0) {
            questionCount = 20;
        }

        switch (sessionType.toUpperCase()) {
            case "ORDER":
                Pageable pageable = PageRequest.of(0, questionCount, Sort.by(Sort.Direction.ASC, "id"));
                return questionRepository.findByStatus(1, pageable).getContent();

            case "RANDOM":
                if (dto.getQuestionType() != null && dto.getCategoryId() != null) {
                    return questionRepository.findRandomQuestionsByCategory(dto.getQuestionType(), dto.getCategoryId(), questionCount);
                } else if (dto.getQuestionType() != null) {
                    return questionRepository.findRandomQuestions(dto.getQuestionType(), questionCount);
                } else {
                    return questionRepository.findRandomQuestions(questionCount);
                }

            case "EXAM":
                return getExamQuestions(questionCount, dto.getCategoryId());

            case "WRONG":
                return getWrongQuestionsForPractice(userId);

            case "FAV":
                return getFavoriteQuestionsForPractice(userId);

            case "TYPE":
                if (dto.getQuestionType() != null && dto.getCategoryId() != null) {
                    return questionRepository.findRandomQuestionsByCategory(dto.getQuestionType(), dto.getCategoryId(), questionCount);
                } else if (dto.getQuestionType() != null) {
                    return questionRepository.findRandomQuestions(dto.getQuestionType(), questionCount);
                } else {
                    return questionRepository.findRandomQuestions(questionCount);
                }

            default:
                return questionRepository.findRandomQuestions(questionCount);
        }
    }

    private List<Question> getExamQuestions(int totalCount, Long categoryId) {
        int singleCount = (int) Math.round(totalCount * 0.6);
        int multiCount = (int) Math.round(totalCount * 0.25);
        int judgeCount = totalCount - singleCount - multiCount;

        List<Question> questions = new ArrayList<>();
        if (categoryId != null) {
            questions.addAll(questionRepository.findRandomQuestionsByCategory("SINGLE", categoryId, singleCount));
            questions.addAll(questionRepository.findRandomQuestionsByCategory("MULTI", categoryId, multiCount));
            questions.addAll(questionRepository.findRandomQuestionsByCategory("JUDGE", categoryId, judgeCount));
        } else {
            questions.addAll(questionRepository.findRandomQuestions("SINGLE", singleCount));
            questions.addAll(questionRepository.findRandomQuestions("MULTI", multiCount));
            questions.addAll(questionRepository.findRandomQuestions("JUDGE", judgeCount));
        }
        Collections.shuffle(questions);
        return questions;
    }

    private List<Question> getWrongQuestionsForPractice(Long userId) {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "lastWrongTime"));
        List<WrongQuestion> wrongQuestions = wrongQuestionRepository.findByUserIdAndStatus(userId, 1, pageable).getContent();
        List<Long> questionIds = wrongQuestions.stream()
                .map(WrongQuestion::getQuestionId)
                .collect(Collectors.toList());
        if (questionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return questionRepository.findAllById(questionIds);
    }

    private List<Question> getFavoriteQuestionsForPractice(Long userId) {
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "createTime"));
        List<com.quizapp.entity.Favorite> favorites = favoriteRepository.findByUserId(userId, pageable).getContent();
        List<Long> questionIds = favorites.stream()
                .map(com.quizapp.entity.Favorite::getQuestionId)
                .collect(Collectors.toList());
        if (questionIds.isEmpty()) {
            return Collections.emptyList();
        }
        return questionRepository.findAllById(questionIds);
    }

    private void updateWrongQuestion(Long userId, Long questionId, boolean isCorrect) {
        Optional<WrongQuestion> existingOpt = wrongQuestionRepository.findByUserIdAndQuestionId(userId, questionId);
        if (isCorrect) {
            if (existingOpt.isPresent()) {
                WrongQuestion wq = existingOpt.get();
                wq.setConsecutiveRight(wq.getConsecutiveRight() + 1);
                wq.setLastAnswerTime(LocalDateTime.now());
                if (wq.getConsecutiveRight() >= 3) {
                    wq.setStatus(0);
                }
                wq.setUpdateTime(LocalDateTime.now());
                wrongQuestionRepository.save(wq);
            }
        } else {
            if (existingOpt.isPresent()) {
                WrongQuestion wq = existingOpt.get();
                wq.setWrongCount(wq.getWrongCount() + 1);
                wq.setConsecutiveRight(0);
                wq.setLastWrongTime(LocalDateTime.now());
                wq.setLastAnswerTime(LocalDateTime.now());
                wq.setStatus(1);
                wq.setUpdateTime(LocalDateTime.now());
                wrongQuestionRepository.save(wq);
            } else {
                WrongQuestion wq = new WrongQuestion();
                wq.setUserId(userId);
                wq.setQuestionId(questionId);
                wq.setWrongCount(1);
                wq.setConsecutiveRight(0);
                wq.setFirstWrongTime(LocalDateTime.now());
                wq.setLastWrongTime(LocalDateTime.now());
                wq.setLastAnswerTime(LocalDateTime.now());
                wq.setStatus(1);
                wq.setCreateTime(LocalDateTime.now());
                wq.setUpdateTime(LocalDateTime.now());
                wrongQuestionRepository.save(wq);
            }
        }
    }

    private void updateQuestionMastery(Long userId, Long questionId, boolean isCorrect) {
        Optional<QuestionMastery> existingOpt = questionMasteryRepository.findByUserIdAndQuestionId(userId, questionId);
        if (existingOpt.isPresent()) {
            QuestionMastery mastery = existingOpt.get();
            mastery.setTotalAttempts(mastery.getTotalAttempts() + 1);
            if (isCorrect) {
                mastery.setCorrectAttempts(mastery.getCorrectAttempts() + 1);
                mastery.setConsecutiveRight(mastery.getConsecutiveRight() + 1);
            } else {
                mastery.setConsecutiveRight(0);
            }
            if (mastery.getConsecutiveRight() >= 3) {
                mastery.setMastered(1);
            }
            mastery.setLastAnswerTime(LocalDateTime.now());
            mastery.setUpdateTime(LocalDateTime.now());
            questionMasteryRepository.save(mastery);
        } else {
            QuestionMastery mastery = new QuestionMastery();
            mastery.setUserId(userId);
            mastery.setQuestionId(questionId);
            mastery.setTotalAttempts(1);
            mastery.setCorrectAttempts(isCorrect ? 1 : 0);
            mastery.setConsecutiveRight(isCorrect ? 1 : 0);
            mastery.setMastered(0);
            mastery.setLastAnswerTime(LocalDateTime.now());
            mastery.setUpdateTime(LocalDateTime.now());
            questionMasteryRepository.save(mastery);
        }
    }

    private void updateDailyCheckin(Long userId) {
        LocalDate today = LocalDate.now();
        Optional<DailyCheckin> existingOpt = dailyCheckinRepository.findByUserIdAndCheckinDate(userId, today);
        if (existingOpt.isPresent()) {
            DailyCheckin checkin = existingOpt.get();
            checkin.setAnswerCount(checkin.getAnswerCount() + 1);
            dailyCheckinRepository.save(checkin);
        } else {
            DailyCheckin checkin = new DailyCheckin();
            checkin.setUserId(userId);
            checkin.setCheckinDate(today);
            checkin.setAnswerCount(1);
            checkin.setCreateTime(LocalDateTime.now());
            dailyCheckinRepository.save(checkin);
        }
    }

    private void updateUserStats(Long userId, boolean isCorrect, Long durationMs) {
        Optional<UserStats> statsOpt = userStatsRepository.findByUserId(userId);
        UserStats stats;
        if (statsOpt.isPresent()) {
            stats = statsOpt.get();
        } else {
            stats = new UserStats();
            stats.setUserId(userId);
            stats.setTotalAnswers(0);
            stats.setTotalCorrect(0);
            stats.setTotalWrong(0);
            stats.setWrongBookCount(0);
            stats.setFavoriteCount(0);
            stats.setTotalDurationSec(0L);
            stats.setStreakDays(0);
        }

        stats.setTotalAnswers(stats.getTotalAnswers() + 1);
        if (isCorrect) {
            stats.setTotalCorrect(stats.getTotalCorrect() + 1);
        } else {
            stats.setTotalWrong(stats.getTotalWrong() + 1);
        }
        if (durationMs != null) {
            stats.setTotalDurationSec(stats.getTotalDurationSec() + durationMs / 1000);
        }

        // Update streak
        LocalDate today = LocalDate.now();
        if (stats.getLastPracticeDate() == null) {
            stats.setStreakDays(1);
        } else if (stats.getLastPracticeDate().equals(today)) {
            // Same day, no change
        } else if (stats.getLastPracticeDate().equals(today.minusDays(1))) {
            stats.setStreakDays(stats.getStreakDays() + 1);
        } else {
            stats.setStreakDays(1);
        }
        stats.setLastPracticeDate(today);
        stats.setUpdateTime(LocalDateTime.now());
        userStatsRepository.save(stats);
    }

    private PracticeSessionVO convertSessionToVO(PracticeSession session) {
        PracticeSessionVO vo = new PracticeSessionVO();
        vo.setSessionId(session.getSessionId());
        vo.setSessionType(session.getSessionType());
        vo.setQuestionType(session.getQuestionType());
        vo.setTotalCount(session.getTotalCount());
        vo.setCorrectCount(session.getCorrectCount());
        vo.setStartTime(session.getStartTime());
        vo.setEndTime(session.getEndTime());
        vo.setDurationSec(session.getDurationSec());
        vo.setStatus(session.getStatus());
        return vo;
    }

    private QuestionVO convertQuestionToVO(Question question, Long userId) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setCategoryId(question.getCategoryId());
        vo.setQuestionType(question.getQuestionType());
        vo.setTitle(question.getTitle());
        vo.setDifficulty(question.getDifficulty());
        vo.setSourceNumber(question.getSourceNumber());
        vo.setCorrectAnswer(question.getCorrectAnswer());
        vo.setExplanation(question.getExplanation());

        if (question.getOptionsJson() != null && !question.getOptionsJson().trim().isEmpty()) {
            try {
                List<OptionVO> options = objectMapper.readValue(question.getOptionsJson(),
                        new TypeReference<List<OptionVO>>() {});
                vo.setOptions(options);
            } catch (Exception e) {
                vo.setOptions(new ArrayList<>());
            }
        } else {
            vo.setOptions(new ArrayList<>());
        }

        if (userId != null) {
            boolean isFavorited = favoriteRepository.existsByUserIdAndQuestionId(userId, question.getId());
            vo.setIsFavorited(isFavorited);
            boolean isWrong = wrongQuestionRepository.findByUserIdAndQuestionId(userId, question.getId())
                    .map(wq -> wq.getStatus() == 1)
                    .orElse(false);
            vo.setIsWrong(isWrong);
        }

        return vo;
    }
}