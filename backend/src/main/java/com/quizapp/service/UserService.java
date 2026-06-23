package com.quizapp.service;

import com.quizapp.common.Result;
import com.quizapp.dto.LoginDTO;
import com.quizapp.dto.RegisterDTO;
import com.quizapp.entity.User;
import com.quizapp.vo.LoginVO;

/**
 * 用户服务接口
 * <p>提供用户注册、登录、个人信息管理等业务操作</p>
 *
 * @author quizapp
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param dto 登录请求参数（手机号、密码）
     * @return 登录结果，包含Token和用户信息
     */
    Result<LoginVO> login(LoginDTO dto);

    /**
     * 用户注册
     *
     * @param dto 注册请求参数（手机号、密码、昵称）
     * @return 注册结果，包含Token和用户信息
     */
    Result<LoginVO> register(RegisterDTO dto);

    /**
     * 获取用户个人信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    Result<User> getProfile(Long userId);

    /**
     * 更新用户个人信息
     *
     * @param userId    用户ID
     * @param nickname  新昵称
     * @param avatarUrl 新头像URL
     * @return 操作结果
     */
    Result<Void> updateProfile(Long userId, String nickname, String avatarUrl);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    Result<Void> changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 注销账号
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> deleteAccount(Long userId);
}