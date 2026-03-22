package com.yagowill.springuserauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yagowill.springuserauth.dto.AuthResponse;
import com.yagowill.springuserauth.dto.LoginRequest;
import com.yagowill.springuserauth.dto.RegisterRequest;
import com.yagowill.springuserauth.security.JwtAuthenticationFilter;
import com.yagowill.springuserauth.security.JwtService;
import com.yagowill.springuserauth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/register - deve registrar com sucesso")
    void shouldRegisterSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("Yago Will")
                .email("yago@email.com")
                .password("123456")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .expiresIn(86400000L)
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.expires_in").value(86400000));
    }

    @Test
    @DisplayName("POST /api/auth/register - deve retornar 400 com dados inválidos")
    void shouldReturn400WhenInvalidRegisterData() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .name("")
                .email("invalid")
                .password("")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login - deve fazer login com sucesso")
    void shouldLoginSuccessfully() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("yago@email.com")
                .password("123456")
                .build();

        AuthResponse response = AuthResponse.builder()
                .token("jwt-token")
                .expiresIn(86400000L)
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }
}
