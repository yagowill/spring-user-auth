package com.yagowill.springuserauth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yagowill.springuserauth.dto.UpdateUserRequest;
import com.yagowill.springuserauth.dto.UserResponse;
import com.yagowill.springuserauth.exception.ResourceNotFoundException;
import com.yagowill.springuserauth.model.UserRole;
import com.yagowill.springuserauth.security.JwtAuthenticationFilter;
import com.yagowill.springuserauth.security.JwtService;
import com.yagowill.springuserauth.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse createUserResponse() {
        return UserResponse.builder()
                .id(UUID.randomUUID())
                .name("Yago Will")
                .email("yago@email.com")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("GET /api/users - deve retornar lista de usuários")
    void shouldReturnAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(createUserResponse()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Yago Will"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - deve retornar usuário por ID")
    void shouldReturnUserById() throws Exception {
        UUID id = UUID.randomUUID();
        UserResponse response = createUserResponse();
        when(userService.findById(id)).thenReturn(response);

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yago Will"));
    }

    @Test
    @DisplayName("GET /api/users/{id} - deve retornar 404 quando não encontrado")
    void shouldReturn404WhenUserNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(userService.findById(id)).thenThrow(new ResourceNotFoundException("Usuário não encontrado"));

        mockMvc.perform(get("/api/users/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/users/{id} - deve atualizar usuário")
    void shouldUpdateUser() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Yago Atualizado")
                .build();

        UserResponse response = UserResponse.builder()
                .id(id)
                .name("Yago Atualizado")
                .email("yago@email.com")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();

        when(userService.update(eq(id), any(UpdateUserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yago Atualizado"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - deve deletar usuário")
    void shouldDeleteUser() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userService).delete(id);

        mockMvc.perform(delete("/api/users/{id}", id))
                .andExpect(status().isNoContent());
    }
}
