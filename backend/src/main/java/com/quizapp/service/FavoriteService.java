package com.quizapp.service;

import com.quizapp.common.Result;
import com.quizapp.vo.FavoriteVO;
import com.quizapp.vo.PageVO;

import java.util.List;

/**
 * 收藏服务接口
 * <p>提供题目收藏/取消收藏、查询收藏列表等业务操作</p>
 *
 * @author quizapp
 */
public interface FavoriteService {

    /**
     * 切换题目收藏状态（收藏则取消，未收藏则收藏）
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return 操作结果
     */
    Result<Void> toggleFavorite(Long userId, Long questionId);

    /**
     * 判断用户是否已收藏指定题目
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     * @return true表示已收藏
     */
    Result<Boolean> isFavorited(Long userId, Long questionId);

    /**
     * 分页查询用户收藏的题目
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 分页收藏列表
     */
    Result<PageVO<FavoriteVO>> getFavorites(Long userId, int page, int size);

    /**
     * 批量取消收藏
     *
     * @param userId      用户ID
     * @param questionIds 题目ID列表
     * @return 操作结果
     */
    Result<Void> batchRemoveFavorites(Long userId, List<Long> questionIds);

    /**
     * 获取用户收藏数量
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    Result<Integer> getFavoriteCount(Long userId);
}