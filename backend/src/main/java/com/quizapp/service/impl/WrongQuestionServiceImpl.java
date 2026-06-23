package com.quizapp.service.impl;

import com.quizapp.common.Result;
import com.quizapp.entity.WrongQuestion;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.WrongQuestionRepository;
import com.quizapp.service.WrongQuestionService;
import com.quizapp.vo.PageVO;
import com.quizapp.vo.WrongQuestionVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 错题本服务实现类
 * <p>实现错题查询、移除、清空等业务逻辑</p>
 *
 * @author quizapp
 */
@Service
public class WrongQuestionServiceImpl implements WrongQuestionService {

    private final WrongQuestionRepository wrongQuestionRepository;
    private final QuestionRepository questionRepository;

    public WrongQuestionServiceImpl(WrongQuestionRepository wrongQuestionRepository,
                                    QuestionRepository questionRepository) {
        this.wrongQuestionRepository = wrongQuestionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Result<PageVO<WrongQuestionVO>> getWrongQuestions(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "lastWrongTime"));
        Page<WrongQuestion> wrongPage = wrongQuestionRepository.findByUserIdAndStatus(userId, 1, pageable);
        List<WrongQuestionVO> voList = wrongPage.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageVO<WrongQuestionVO> pageVO = new PageVO<>();
        pageVO.setContent(voList);
        pageVO.setTotalElements(wrongPage.getTotalElements());
        pageVO.setTotalPages(wrongPage.getTotalPages());
        pageVO.setCurrentPage(page);
        pageVO.setSize(size);
        return Result.success(pageVO);
    }

    @Override
    @Transactional
    public Result<Void> removeWrongQuestion(Long userId, Long questionId) {
        wrongQuestionRepository.findByUserIdAndQuestionId(userId, questionId)
                .ifPresent(wq -> {
                    wq.setStatus(0);
                    wq.setUpdateTime(java.time.LocalDateTime.now());
                    wrongQuestionRepository.save(wq);
                });
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result<Void> batchRemoveWrongQuestions(Long userId, List<Long> questionIds) {
        wrongQuestionRepository.deleteByUserIdAndQuestionIds(userId, questionIds);
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result<Void> clearAllWrongQuestions(Long userId) {
        Pageable pageable = PageRequest.of(0, 10000);
        List<WrongQuestion> wrongQuestions = wrongQuestionRepository.findByUserIdAndStatus(userId, 1, pageable).getContent();
        for (WrongQuestion wq : wrongQuestions) {
            wq.setStatus(0);
            wq.setUpdateTime(java.time.LocalDateTime.now());
        }
        wrongQuestionRepository.saveAll(wrongQuestions);
        return Result.success(null);
    }

    @Override
    public Result<Integer> getWrongCount(Long userId) {
        int count = wrongQuestionRepository.countByUserIdAndStatus(userId, 1);
        return Result.success(count);
    }

    private WrongQuestionVO convertToVO(WrongQuestion wq) {
        WrongQuestionVO vo = new WrongQuestionVO();
        vo.setId(wq.getId());
        vo.setQuestionId(wq.getQuestionId());
        vo.setWrongCount(wq.getWrongCount());
        vo.setConsecutiveRight(wq.getConsecutiveRight());
        vo.setLastWrongTime(wq.getLastWrongTime());

        questionRepository.findById(wq.getQuestionId()).ifPresent(q -> {
            vo.setTitle(q.getTitle());
            vo.setQuestionType(q.getQuestionType());
        });

        return vo;
    }
}