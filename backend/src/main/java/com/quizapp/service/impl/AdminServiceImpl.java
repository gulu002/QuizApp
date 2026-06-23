package com.quizapp.service.impl;

import com.quizapp.common.Result;
import com.quizapp.dto.QuestionQueryDTO;
import com.quizapp.entity.AnswerImport;
import com.quizapp.entity.Category;
import com.quizapp.entity.Question;
import com.quizapp.entity.User;
import com.quizapp.repository.AnswerImportRepository;
import com.quizapp.repository.AnswerRecordRepository;
import com.quizapp.repository.CategoryRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.repository.UserStatsRepository;
import com.quizapp.service.AdminService;
import com.quizapp.vo.PageVO;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final AnswerImportRepository answerImportRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final UserStatsRepository userStatsRepository;

    public AdminServiceImpl(QuestionRepository questionRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            AnswerImportRepository answerImportRepository,
                            AnswerRecordRepository answerRecordRepository,
                            UserStatsRepository userStatsRepository) {
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.answerImportRepository = answerImportRepository;
        this.answerRecordRepository = answerRecordRepository;
        this.userStatsRepository = userStatsRepository;
    }

    @Override
    public Result<PageVO<Question>> adminQueryQuestions(QuestionQueryDTO dto) {
        int page = dto.getPage() != null ? dto.getPage() : 1;
        int size = dto.getSize() != null ? dto.getSize() : 20;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));

        Specification<Question> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
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
        PageVO<Question> pageVO = new PageVO<>();
        pageVO.setContent(questionPage.getContent());
        pageVO.setTotalElements(questionPage.getTotalElements());
        pageVO.setTotalPages(questionPage.getTotalPages());
        pageVO.setCurrentPage(page);
        pageVO.setSize(size);
        return Result.success(pageVO);
    }

    @Override
    @Transactional
    public Result<Question> adminCreateQuestion(Question question) {
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());
        if (question.getStatus() == null) {
            question.setStatus(1);
        }
        questionRepository.save(question);
        return Result.success(question);
    }

    @Override
    @Transactional
    public Result<Question> adminUpdateQuestion(Long id, Question question) {
        Optional<Question> existingOpt = questionRepository.findById(id);
        if (!existingOpt.isPresent()) {
            return Result.error("题目不存在");
        }
        Question existing = existingOpt.get();
        existing.setCategoryId(question.getCategoryId());
        existing.setQuestionType(question.getQuestionType());
        existing.setTitle(question.getTitle());
        existing.setOptionsJson(question.getOptionsJson());
        existing.setCorrectAnswer(question.getCorrectAnswer());
        existing.setExplanation(question.getExplanation());
        existing.setDifficulty(question.getDifficulty());
        existing.setSourceNumber(question.getSourceNumber());
        existing.setStatus(question.getStatus());
        existing.setUpdateTime(LocalDateTime.now());
        questionRepository.save(existing);
        return Result.success(existing);
    }

    @Override
    @Transactional
    public Result<Void> adminDeleteQuestion(Long id) {
        questionRepository.deleteById(id);
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result<Void> adminToggleQuestionStatus(Long id, Integer status) {
        Optional<Question> questionOpt = questionRepository.findById(id);
        if (!questionOpt.isPresent()) {
            return Result.error("题目不存在");
        }
        Question question = questionOpt.get();
        question.setStatus(status);
        question.setUpdateTime(LocalDateTime.now());
        questionRepository.save(question);
        return Result.success(null);
    }

    @Override
    public Result<PageVO<User>> adminQueryUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<User> userPage = userRepository.findAll(pageable);
        userPage.forEach(u -> u.setPasswordHash(null));

        PageVO<User> pageVO = new PageVO<>();
        pageVO.setContent(userPage.getContent());
        pageVO.setTotalElements(userPage.getTotalElements());
        pageVO.setTotalPages(userPage.getTotalPages());
        pageVO.setCurrentPage(page);
        pageVO.setSize(size);
        return Result.success(pageVO);
    }

    @Override
    @Transactional
    public Result<Void> adminToggleUserStatus(Long userId, Integer status) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }
        User user = userOpt.get();
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return Result.success(null);
    }

    @Override
    public Result<List<Category>> adminGetCategories() {
        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        return Result.success(categories);
    }

    @Override
    @Transactional
    public Result<Category> adminCreateCategory(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        categoryRepository.save(category);
        return Result.success(category);
    }

    @Override
    @Transactional
    public Result<Category> adminUpdateCategory(Long id, Category category) {
        Optional<Category> existingOpt = categoryRepository.findById(id);
        if (!existingOpt.isPresent()) {
            return Result.error("分类不存在");
        }
        Category existing = existingOpt.get();
        existing.setName(category.getName());
        existing.setParentId(category.getParentId());
        existing.setSortOrder(category.getSortOrder());
        existing.setUpdateTime(LocalDateTime.now());
        categoryRepository.save(existing);
        return Result.success(existing);
    }

    @Override
    @Transactional
    public Result<Void> adminDeleteCategory(Long id) {
        categoryRepository.deleteById(id);
        return Result.success(null);
    }

    @Override
    public Result<Map<String, Object>> adminGetDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalQuestions", questionRepository.count());
        stats.put("totalCategories", categoryRepository.count());
        stats.put("totalAnswerRecords", answerRecordRepository.count());
        stats.put("totalPendingImports", answerImportRepository.countByImported(0));
        return Result.success(stats);
    }

    @Override
    @Transactional
    public Result<Void> adminSyncAnswers() {
        List<AnswerImport> imports = answerImportRepository.findByImported(0);
        for (AnswerImport answerImport : imports) {
            Optional<Question> questionOpt = questionRepository.findBySourceNumber(answerImport.getSourceNumber());
            if (questionOpt.isPresent()) {
                Question question = questionOpt.get();
                question.setCorrectAnswer(answerImport.getCorrectAnswer());
                question.setExplanation(answerImport.getExplanation());
                question.setUpdateTime(LocalDateTime.now());
                questionRepository.save(question);
                answerImport.setImported(1);
                answerImportRepository.save(answerImport);
            }
        }
        return Result.success(null);
    }
}