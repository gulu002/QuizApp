package com.quizapp.service.impl;

import com.quizapp.common.Result;
import com.quizapp.entity.Favorite;
import com.quizapp.repository.FavoriteRepository;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.UserStatsRepository;
import com.quizapp.service.FavoriteService;
import com.quizapp.vo.FavoriteVO;
import com.quizapp.vo.PageVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 收藏服务实现类
 * <p>实现题目收藏/取消收藏、查询收藏列表等业务逻辑</p>
 *
 * @author quizapp
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final QuestionRepository questionRepository;
    private final UserStatsRepository userStatsRepository;

    public FavoriteServiceImpl(FavoriteRepository favoriteRepository,
                               QuestionRepository questionRepository,
                               UserStatsRepository userStatsRepository) {
        this.favoriteRepository = favoriteRepository;
        this.questionRepository = questionRepository;
        this.userStatsRepository = userStatsRepository;
    }

    @Override
    @Transactional
    public Result<Void> toggleFavorite(Long userId, Long questionId) {
        Optional<Favorite> existingOpt = favoriteRepository.findByUserIdAndQuestionId(userId, questionId);
        if (existingOpt.isPresent()) {
            favoriteRepository.delete(existingOpt.get());
            updateUserStatsFavoriteCount(userId);
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setQuestionId(questionId);
            favorite.setCreateTime(LocalDateTime.now());
            favoriteRepository.save(favorite);
            updateUserStatsFavoriteCount(userId);
        }
        return Result.success(null);
    }

    @Override
    public Result<Boolean> isFavorited(Long userId, Long questionId) {
        boolean exists = favoriteRepository.existsByUserIdAndQuestionId(userId, questionId);
        return Result.success(exists);
    }

    @Override
    public Result<PageVO<FavoriteVO>> getFavorites(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Favorite> favoritePage = favoriteRepository.findByUserId(userId, pageable);
        List<FavoriteVO> voList = favoritePage.getContent().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        PageVO<FavoriteVO> pageVO = new PageVO<>();
        pageVO.setContent(voList);
        pageVO.setTotalElements(favoritePage.getTotalElements());
        pageVO.setTotalPages(favoritePage.getTotalPages());
        pageVO.setCurrentPage(page);
        pageVO.setSize(size);
        return Result.success(pageVO);
    }

    @Override
    @Transactional
    public Result<Void> batchRemoveFavorites(Long userId, List<Long> questionIds) {
        favoriteRepository.deleteByUserIdAndQuestionIds(userId, questionIds);
        updateUserStatsFavoriteCount(userId);
        return Result.success(null);
    }

    @Override
    public Result<Integer> getFavoriteCount(Long userId) {
        int count = favoriteRepository.countByUserId(userId);
        return Result.success(count);
    }

    private void updateUserStatsFavoriteCount(Long userId) {
        userStatsRepository.findByUserId(userId).ifPresent(stats -> {
            int count = favoriteRepository.countByUserId(userId);
            stats.setFavoriteCount(count);
            stats.setUpdateTime(LocalDateTime.now());
            userStatsRepository.save(stats);
        });
    }

    private FavoriteVO convertToVO(Favorite favorite) {
        FavoriteVO vo = new FavoriteVO();
        vo.setId(favorite.getId());
        vo.setQuestionId(favorite.getQuestionId());
        vo.setCreateTime(favorite.getCreateTime());

        questionRepository.findById(favorite.getQuestionId()).ifPresent(q -> {
            vo.setTitle(q.getTitle());
            vo.setQuestionType(q.getQuestionType());
        });

        return vo;
    }
}