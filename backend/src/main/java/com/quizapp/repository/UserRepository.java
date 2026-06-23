package com.quizapp.repository;

import com.quizapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 * <p>提供用户表的基本CRUD操作及业务查询方法</p>
 *
 * @author quizapp
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据手机号查找用户
     *
     * @param phone 手机号
     * @return 用户信息，不存在则返回空
     */
    Optional<User> findByPhone(String phone);

    /**
     * 根据微信OpenID查找用户
     *
     * @param openid 微信OpenID
     * @return 用户信息，不存在则返回空
     */
    Optional<User> findByWechatOpenid(String openid);

    /**
     * 判断手机号是否已存在
     *
     * @param phone 手机号
     * @return true表示已存在
     */
    boolean existsByPhone(String phone);
}