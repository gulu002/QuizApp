package com.quizapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizapp.dto.LoginDTO;
import com.quizapp.dto.RegisterDTO;
import com.quizapp.entity.User;
import com.quizapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private String testPhone = "13800138000";
    private String testPassword = "123456";

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        User user = new User();
        user.setPhone(testPhone);
        user.setPasswordHash(passwordEncoder.encode(testPassword));
        user.setNickname("测试用户");
        user.setStatus(1);
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());
        userRepository.save(user);
    }

    @Test
    @DisplayName("注册 - 成功")
    void testRegisterSuccess() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setPhone("13900139000");
        dto.setPassword("test123");
        dto.setNickname("新用户");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.userId").exists());
    }

    @Test
    @DisplayName("注册 - 手机号已存在")
    void testRegisterDuplicatePhone() throws Exception {
        RegisterDTO dto = new RegisterDTO();
        dto.setPhone(testPhone);
        dto.setPassword("test123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("登录 - 成功")
    void testLoginSuccess() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setPhone(testPhone);
        dto.setPassword(testPassword);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.nickname").value("测试用户"));
    }

    @Test
    @DisplayName("登录 - 密码错误")
    void testLoginWrongPassword() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setPhone(testPhone);
        dto.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("登录 - 手机号不存在")
    void testLoginPhoneNotExist() throws Exception {
        LoginDTO dto = new LoginDTO();
        dto.setPhone("19900199000");
        dto.setPassword("123456");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    @DisplayName("获取个人信息 - 需要Token")
    void testGetProfile() throws Exception {
        // 先登录获取token
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setPhone(testPhone);
        loginDTO.setPassword(testPassword);
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(response).get("data").get("token").asText();

        mockMvc.perform(get("/api/auth/profile")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.nickname").value("测试用户"));
    }

    @Test
    @DisplayName("获取个人信息 - 无Token返回401")
    void testGetProfileWithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }
}