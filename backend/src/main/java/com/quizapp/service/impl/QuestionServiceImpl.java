package com.quizapp.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.common.Result;
import com.quizapp.dto.BatchImportDTO;
import com.quizapp.dto.QuestionQueryDTO;
import com.quizapp.entity.AnswerImport;
import com.quizapp.entity.Question;
import com.quizapp.repository.AnswerImportRepository;
import com.quizapp.repository.CategoryRepository;
import com.quizapp.repository.FavoriteRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.WrongQuestionRepository;
import com.quizapp.service.QuestionService;
import com.quizapp.vo.OptionVO;
import com.quizapp.vo.PageVO;
import com.quizapp.vo.QuestionVO;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 题目服务实现类
 * <p>实现题目查询、详情、导入、统计等业务逻辑</p>
 *
 * @author quizapp
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final FavoriteRepository favoriteRepository;
    private final WrongQuestionRepository wrongQuestionRepository;
    private final AnswerImportRepository answerImportRepository;
    private final ObjectMapper objectMapper;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               CategoryRepository categoryRepository,
                               FavoriteRepository favoriteRepository,
                               WrongQuestionRepository wrongQuestionRepository,
                               AnswerImportRepository answerImportRepository,
                               ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.favoriteRepository = favoriteRepository;
        this.wrongQuestionRepository = wrongQuestionRepository;
        this.answerImportRepository = answerImportRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Result<PageVO<QuestionVO>> queryQuestions(QuestionQueryDTO dto) {
        int page = dto.getPage() != null ? dto.getPage() : 1;
        int size = dto.getSize() != null ? dto.getSize() : 20;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        Specification<Question> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("status"), 1));

            if (dto.getQuestionType() != null && !dto.getQuestionType().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("questionType"), dto.getQuestionType()));
            }
            if (dto.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("categoryId"), dto.getCategoryId()));
            }
            if (dto.getKeyword() != null && !dto.getKeyword().trim().isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + dto.getKeyword() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Question> questionPage = questionRepository.findAll(spec, pageable);
        List<QuestionVO> voList = questionPage.getContent().stream()
                .map(q -> convertToVO(q, null))
                .collect(Collectors.toList());

        PageVO<QuestionVO> pageVO = new PageVO<>();
        pageVO.setContent(voList);
        pageVO.setTotalElements(questionPage.getTotalElements());
        pageVO.setTotalPages(questionPage.getTotalPages());
        pageVO.setCurrentPage(page);
        pageVO.setSize(size);
        return Result.success(pageVO);
    }

    @Override
    public Result<QuestionVO> getQuestionDetail(Long questionId, Long userId) {
        Optional<Question> questionOpt = questionRepository.findById(questionId);
        if (!questionOpt.isPresent()) {
            return Result.error("题目不存在");
        }
        Question question = questionOpt.get();
        QuestionVO vo = convertToVO(question, userId);

        String categoryName = null;
        if (question.getCategoryId() != null) {
            categoryName = categoryRepository.findById(question.getCategoryId())
                    .map(c -> c.getName())
                    .orElse(null);
        }
        vo.setCategoryName(categoryName);

        return Result.success(vo);
    }

    @Override
    public Result<List<QuestionVO>> getQuestionsByIds(List<Long> ids) {
        List<Question> questions = questionRepository.findAllById(ids);
        List<QuestionVO> voList = questions.stream()
                .map(q -> convertToVO(q, null))
                .collect(Collectors.toList());
        return Result.success(voList);
    }

    @Override
    @Transactional
    public Result<Void> importAnswers(List<BatchImportDTO> items) {
        for (BatchImportDTO item : items) {
            Optional<Question> questionOpt = questionRepository.findBySourceNumber(item.getSourceNumber());
            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                question.setCorrectAnswer(item.getCorrectAnswer());
                question.setExplanation(item.getExplanation());
                question.setUpdateTime(java.time.LocalDateTime.now());
                questionRepository.save(question);

                Optional<AnswerImport> importOpt = answerImportRepository.findBySourceNumber(item.getSourceNumber());
                if (importOpt.isPresent()) {
                    AnswerImport answerImport = importOpt.get();
                    answerImport.setImported(1);
                    answerImportRepository.save(answerImport);
                }
            }
        }
        return Result.success(null);
    }

    @Override
    public Result<Long> countByType(String questionType) {
        long count = questionRepository.countByQuestionType(questionType);
        return Result.success(count);
    }

    @Override
    public Result<List<Map<String, Object>>> getCategoryStats() {
        Specification<Question> spec = (root, query, cb) -> cb.equal(root.get("status"), 1);
        List<Question> questions = questionRepository.findAll(spec);
        Map<Long, Long> categoryCountMap = new HashMap<>();
        for (Question q : questions) {
            categoryCountMap.merge(q.getCategoryId(), 1L, Long::sum);
        }
        List<Map<String, Object>> stats = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : categoryCountMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("categoryId", entry.getKey());
            item.put("count", entry.getValue());
            String categoryName = categoryRepository.findById(entry.getKey())
                    .map(c -> c.getName())
                    .orElse("未知分类");
            item.put("categoryName", categoryName);
            stats.add(item);
        }
        return Result.success(stats);
    }

    private QuestionVO convertToVO(Question question, Long userId) {
        QuestionVO vo = new QuestionVO();
        vo.setId(question.getId());
        vo.setCategoryId(question.getCategoryId());
        vo.setQuestionType(question.getQuestionType());
        vo.setTitle(question.getTitle());
        vo.setDifficulty(question.getDifficulty());
        vo.setSourceNumber(question.getSourceNumber());

        // Parse optionsJson
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

        // correctAnswer and explanation are always shown in detail
        vo.setCorrectAnswer(question.getCorrectAnswer());
        vo.setExplanation(question.getExplanation());

        // Check favorite and wrong question status
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