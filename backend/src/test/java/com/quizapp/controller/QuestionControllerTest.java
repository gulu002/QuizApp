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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class QuestionControllerTest {

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

    @BeforeEach
    void setUp() {
        // 创建测试用户
        User user = new User();
        user.setPhone("13800138000");
        user.setPasswordHash("test");
        user.setNickname("测试");
        user.setStatus(1);
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());
        user = userRepository.save(user);
        token = jwtUtil.generateToken(user.getId());

        // 创建测试题目
        for (int i = 1; i <= 15; i++) {
            Question q = new Question();
            q.setQuestionType(i <= 8 ? "SINGLE" : i <= 12 ? "MULTI" : "JUDGE");
            q.setTitle("测试题目 " + i);
            q.setOptionsJson("[{\"label\":\"A\",\"text\":\"选项A\"},{\"label\":\"B\",\"text\":\"选项B\"},{\"label\":\"C\",\"text\":\"选项C\"},{\"label\":\"D\",\"text\":\"选项D\"}]");
            q.setCorrectAnswer("A");
            q.setExplanation("这是解析" + i);
            q.setDifficulty(1);
            q.setSourceNumber(i);
            q.setStatus(1);
            q.setCreateTime(java.time.LocalDateTime.now());
            q.setUpdateTime(java.time.LocalDateTime.now());
            questionRepository.save(q);
        }
    }

    @Test
    @DisplayName("查询题目列表 - 默认分页")
    void testQueryQuestions() throws Exception {
        mockMvc.perform(get("/api/questions")
                .header("Authorization", "Bearer " + token)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(15));
    }

    @Test
    @DisplayName("查询题目列表 - 按题型筛选")
    void testQueryQuestionsByType() throws Exception {
        mockMvc.perform(get("/api/questions")
                .header("Authorization", "Bearer " + token)
                .param("questionType", "SINGLE")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalElements").value(8));
    }

    @Test
    @DisplayName("查询题目列表 - 按关键词搜索")
    void testQueryQuestionsByKeyword() throws Exception {
        mockMvc.perform(get("/api/questions")
                .header("Authorization", "Bearer " + token)
                .param("keyword", "测试题目 1")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @DisplayName("获取题目详情")
    void testGetQuestionDetail() throws Exception {
        Question first = questionRepository.findAll().get(0);
        mockMvc.perform(get("/api/questions/" + first.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("测试题目 1"))
                .andExpect(jsonPath("$.data.correctAnswer").value("A"))
                .andExpect(jsonPath("$.data.options").isArray());
    }

    @Test
    @DisplayName("获取题目详情 - 题目不存在")
    void testGetQuestionDetailNotFound() throws Exception {
        mockMvc.perform(get("/api/questions/99999")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("按题型统计数量")
    void testCountByType() throws Exception {
        mockMvc.perform(get("/api/questions/count")
                .header("Authorization", "Bearer " + token)
                .param("questionType", "SINGLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(8));
    }
}