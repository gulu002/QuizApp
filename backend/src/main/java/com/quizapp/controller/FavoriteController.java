package com.quizapp.controller;

import com.quizapp.common.Result;
import com.quizapp.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 收藏控制器
 * <p>处理题目收藏/取消收藏、查询收藏列表等请求</p>
 *
 * @author quizapp
 */
@RestController
@RequestMapping("/api/favorites")
public class FavoriteController extends BaseController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/toggle/{questionId}")
    public Result<?> toggleFavorite(@PathVariable Long questionId) {
        Long userId = getCurrentUserId();
        return favoriteService.toggleFavorite(userId, questionId);
    }

    @GetMapping("/check/{questionId}")
    public Result<?> isFavorited(@PathVariable Long questionId) {
        Long userId = getCurrentUserId();
        return favoriteService.isFavorited(userId, questionId);
    }

    @GetMapping
    public Result<?> getFavorites(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Long userId = getCurrentUserId();
        return favoriteService.getFavorites(userId, page, size);
    }

    @DeleteMapping("/batch")
    public Result<?> batchRemoveFavorites(@RequestBody List<Long> questionIds) {
        Long userId = getCurrentUserId();
        return favoriteService.batchRemoveFavorites(userId, questionIds);
    }

    @GetMapping("/count")
    public Result<?> getFavoriteCount() {
        Long userId = getCurrentUserId();
        return favoriteService.getFavoriteCount(userId);
    }
}