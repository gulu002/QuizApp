package com.quizapp.service.impl;

import com.quizapp.common.Result;
import com.quizapp.dto.LoginDTO;
import com.quizapp.dto.RegisterDTO;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import com.quizapp.service.UserService;
import com.quizapp.security.JwtUtil;
import com.quizapp.vo.LoginVO;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 用户服务实现类
 * <p>实现用户注册、登录、个人信息管理等业务逻辑</p>
 *
 * @author quizapp
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Result<LoginVO> login(LoginDTO dto) {
        Optional<User> userOpt = userRepository.findByPhone(dto.getPhone());
        if (!userOpt.isPresent()) {
            return Result.error("手机号未注册");
        }
        User user = userOpt.get();
        if (user.getStatus() == 0) {
            return Result.error("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            return Result.error("密码错误");
        }
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        return Result.success(vo);
    }

    @Override
    @Transactional
    public Result<LoginVO> register(RegisterDTO dto) {
        if (userRepository.existsByPhone(dto.getPhone())) {
            return Result.error("手机号已注册");
        }
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : "用户" + dto.getPhone().substring(7));
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId());
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatarUrl(user.getAvatarUrl());
        return Result.success(vo);
    }

    @Override
    public Result<User> getProfile(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }
        User user = userOpt.get();
        user.setPasswordHash(null);
        return Result.success(user);
    }

    @Override
    @Transactional
    public Result<Void> updateProfile(Long userId, String nickname, String avatarUrl) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }
        User user = userOpt.get();
        if (nickname != null && !nickname.trim().isEmpty()) {
            user.setNickname(nickname);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result<Void> changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }
        User user = userOpt.get();
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            return Result.error("原密码错误");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result<Void> deleteAccount(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }
        User user = userOpt.get();
        user.setStatus(0);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        return Result.success(null);
    }
}