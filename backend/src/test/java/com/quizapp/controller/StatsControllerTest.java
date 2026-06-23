package com.quizapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private String token;

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
    }

    @Test
    @DisplayName("获取用户统计概览")
    void testGetUserStats() throws Exception {
        mockMvc.perform(get("/api/stats/overview")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalAnswers").value(0))
                .andExpect(jsonPath("$.data.correctRate").value(0.0));
    }

    @Test
    @DisplayName("按题型统计")
    void testGetStatsByType() throws Exception {
        mockMvc.perform(get("/api/stats/by-type")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("按学科统计")
    void testGetStatsByCategory() throws Exception {
        mockMvc.perform(get("/api/stats/by-category")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("学习日历")
    void testGetStudyCalendar() throws Exception {
        mockMvc.perform(get("/api/stats/calendar")
                .header("Authorization", "Bearer " + token)
                .param("year", "2026")
                .param("month", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("薄弱分析")
    void testGetWeaknessAnalysis() throws Exception {
        mockMvc.perform(get("/api/stats/weakness")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}