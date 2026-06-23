package com.quizapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.dto.FinishSessionDTO;
import com.quizapp.dto.PracticeStartDTO;
import com.quizapp.dto.SubmitAnswerDTO;
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
class PracticeControllerTest {

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
    private String sessionId;

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
        for (int i = 1; i <= 20; i++) {
            Question q = new Question();
            q.setQuestionType(i <= 12 ? "SINGLE" : i <= 17 ? "MULTI" : "JUDGE");
            q.setTitle("测试题目 " + i);
            q.setOptionsJson("[{\"label\":\"A\",\"text\":\"选项A\"},{\"label\":\"B\",\"text\":\"选项B\"},{\"label\":\"C\",\"text\":\"选项C\"},{\"label\":\"D\",\"text\":\"选项D\"}]");
            q.setCorrectAnswer("A");
            q.setExplanation("解析" + i);
            q.setDifficulty(1);
            q.setStatus(1);
            q.setCreateTime(java.time.LocalDateTime.now());
            q.setUpdateTime(java.time.LocalDateTime.now());
            questionRepository.save(q);
        }
    }

    @Test
    @DisplayName("开始练习 - 随机模式")
    void testStartPracticeRandom() throws Exception {
        PracticeStartDTO dto = new PracticeStartDTO();
        dto.setSessionType("RANDOM");
        dto.setQuestionCount(10);

        String response = mockMvc.perform(post("/api/practice/start")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sessionId").exists())
                .andExpect(jsonPath("$.data.totalCount").value(10))
                .andReturn().getResponse().getContentAsString();

        sessionId = objectMapper.readTree(response).get("data").get("sessionId").asText();
    }

    @Test
    @DisplayName("开始练习 - 顺序模式")
    void testStartPracticeOrder() throws Exception {
        PracticeStartDTO dto = new PracticeStartDTO();
        dto.setSessionType("ORDER");
        dto.setQuestionCount(20);

        mockMvc.perform(post("/api/practice/start")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sessionId").exists());
    }

    @Test
    @DisplayName("开始练习 - 题型专练")
    void testStartPracticeType() throws Exception {
        PracticeStartDTO dto = new PracticeStartDTO();
        dto.setSessionType("TYPE");
        dto.setQuestionType("SINGLE");
        dto.setQuestionCount(10);

        mockMvc.perform(post("/api/practice/start")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalCount").value(10));
    }

    @Test
    @DisplayName("开始练习 - 模拟考试")
    void testStartPracticeExam() throws Exception {
        PracticeStartDTO dto = new PracticeStartDTO();
        dto.setSessionType("EXAM");
        dto.setQuestionCount(20);
        dto.setTimeLimitMin(90);

        mockMvc.perform(post("/api/practice/start")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.sessionId").exists());
    }

    @Test
    @DisplayName("提交答案 - 正确")
    void testSubmitCorrectAnswer() throws Exception {
        // 先开始练习
        PracticeStartDTO startDTO = new PracticeStartDTO();
        startDTO.setSessionType("RANDOM");
        startDTO.setQuestionCount(10);
        String startResp = mockMvc.perform(post("/api/practice/start")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(startDTO)))
                .andReturn().getResponse().getContentAsString();
        String sid = objectMapper.readTree(startResp).get("data").get("sessionId").asText();

        // 提交答案
        Question firstQ = questionRepository.findAll().get(0);
        SubmitAnswerDTO submitDTO = new SubmitAnswerDTO();
        submitDTO.setSessionId(sid);
        submitDTO.setQuestionId(firstQ.getId());
        submitDTO.setUserAnswer("A");
        submitDTO.setDurationMs(5000);

        mockMvc.perform(post("/api/practice/submit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submitDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.isCorrect").value(true))
                .andExpect(jsonPath("$.data.correctAnswer").value("A"));
    }

    @Test
    @DisplayName("提交答案 - 错误")
    void testSubmitWrongAnswer() throws Exception {
        PracticeStartDTO startDTO = new PracticeStartDTO();
        startDTO.setSessionType("RANDOM");
        startDTO.setQuestionCount(10);
        String startResp = mockMvc.perform(post("/api/practice/start")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(startDTO)))
                .andReturn().getResponse().getContentAsString();
        String sid = objectMapper.readTree(startResp).get("data").get("sessionId").asText();

        Question firstQ = questionRepository.findAll().get(0);
        SubmitAnswerDTO submitDTO = new SubmitAnswerDTO();
        submitDTO.setSessionId(sid);
        submitDTO.setQuestionId(firstQ.getId());
        submitDTO.setUserAnswer("B");
        submitDTO.setDurationMs(3000);

        mockMvc.perform(post("/api/practice/submit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submitDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.isCorrect").value(false))
                .andExpect(jsonPath("$.data.correctAnswer").value("A"));
    }

    @Test
    @DisplayName("提交答案 - 会话不存在")
    void testSubmitAnswerSessionNotFound() throws Exception {
        SubmitAnswerDTO submitDTO = new SubmitAnswerDTO();
        submitDTO.setSessionId("nonexistent-session-id");
        submitDTO.setQuestionId(1L);
        submitDTO.setUserAnswer("A");

        mockMvc.perform(post("/api/practice/submit")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submitDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("结束练习")
    void testFinishPractice() throws Exception {
        PracticeStartDTO startDTO = new PracticeStartDTO();
        startDTO.setSessionType("RANDOM");
        startDTO.setQuestionCount(10);
        String startResp = mockMvc.perform(post("/api/practice/start")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(startDTO)))
                .andReturn().getResponse().getContentAsString();
        String sid = objectMapper.readTree(startResp).get("data").get("sessionId").asText();

        FinishSessionDTO finishDTO = new FinishSessionDTO();
        finishDTO.setSessionId(sid);

        mockMvc.perform(post("/api/practice/finish")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(finishDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.status").value(2));
    }

    @Test
    @DisplayName("获取练习历史")
    void testGetSessionHistory() throws Exception {
        mockMvc.perform(get("/api/practice/history")
                .header("Authorization", "Bearer " + token)
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}