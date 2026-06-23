package com.quizapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.entity.Question;
import com.quizapp.entity.User;
import com.quizapp.repository.QuestionRepository;
import com.quizapp.repository.UserRepository;
import com.quizapp.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class FavoriteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;
    private Long questionId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setPhone("13800138000");
        user.setPasswordHash("test");
        user.setNickname("测试");
        user.setStatus(1);
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());
        user = userRepository.save(user);
        token = jwtUtil.generateToken(user.getId());

        Question q = new Question();
        q.setQuestionType("SINGLE");
        q.setTitle("收藏测试题目");
        q.setOptionsJson("[{\"label\":\"A\",\"text\":\"选项A\"}]");
        q.setCorrectAnswer("A");
        q.setStatus(1);
        q.setCreateTime(java.time.LocalDateTime.now());
        q.setUpdateTime(java.time.LocalDateTime.now());
        questionId = questionRepository.save(q).getId();
    }

    @Test
    @DisplayName("收藏题目")
    void testToggleFavoriteAdd() throws Exception {
        mockMvc.perform(post("/api/favorites/toggle/" + questionId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("取消收藏")
    void testToggleFavoriteRemove() throws Exception {
        // 先收藏
        mockMvc.perform(post("/api/favorites/toggle/" + questionId)
                .header("Authorization", "Bearer " + token));
        // 再取消
        mockMvc.perform(post("/api/favorites/toggle/" + questionId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("检查是否收藏")
    void testIsFavorited() throws Exception {
        mockMvc.perform(get("/api/favorites/check/" + questionId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("获取收藏列表")
    void testGetFavorites() throws Exception {
        mockMvc.perform(get("/api/favorites")
                .header("Authorization", "Bearer " + token)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取收藏数量")
    void testGetFavoriteCount() throws Exception {
        mockMvc.perform(get("/api/favorites/count")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(0));
    }
}