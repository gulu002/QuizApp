package com.quizapp.controller;

import com.quizapp.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 基础控制器
 * <p>提供所有控制器的通用方法，如获取当前登录用户ID</p>
 *
 * @author quizapp
 */
public abstract class BaseController {

    /**
     * 获取当前登录用户ID
     * <p>从Spring Security上下文中获取已认证用户的ID</p>
     *
     * @return 当前用户ID
     * @throws BusinessException 当用户未登录时抛出401异常
     */
    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof Long) {
                return (Long) principal;
            }
            if (principal instanceof String) {
                try {
                    return Long.valueOf((String) principal);
                } catch (NumberFormatException e) {
                    // Not a valid user ID
                }
            }
        }
        throw new BusinessException(401, "未登录");
    }
}