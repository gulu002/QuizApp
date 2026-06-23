package com.quizapp.controller;

import com.quizapp.common.Result;
import com.quizapp.dto.LoginDTO;
import com.quizapp.dto.RegisterDTO;
import com.quizapp.service.UserService;
import com.quizapp.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * <p>处理用户登录、注册、个人信息管理等请求</p>
 *
 * @author quizapp
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController extends BaseController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @GetMapping("/profile")
    public Result<?> getProfile() {
        Long userId = getCurrentUserId();
        return userService.getProfile(userId);
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestHeader("Authorization") String token,
                                   @RequestParam String nickname,
                                   @RequestParam(required = false) String avatarUrl) {
        Long userId = getUserIdFromToken(token);
        return userService.updateProfile(userId, nickname, avatarUrl);
    }

    @PutMapping("/password")
    public Result<?> changePassword(@RequestHeader("Authorization") String token,
                                    @RequestParam String oldPassword,
                                    @RequestParam String newPassword) {
        Long userId = getUserIdFromToken(token);
        return userService.changePassword(userId, oldPassword, newPassword);
    }

    @DeleteMapping("/account")
    public Result<?> deleteAccount(@RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);
        return userService.deleteAccount(userId);
    }

    private Long getUserIdFromToken(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return jwtUtil.getUserIdFromToken(token);
    }
}