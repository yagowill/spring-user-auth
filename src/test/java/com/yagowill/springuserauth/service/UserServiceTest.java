package com.yagowill.springuserauth.service;

import com.yagowill.springuserauth.dto.UpdateUserRequest;
import com.yagowill.springuserauth.dto.UserResponse;
import com.yagowill.springuserauth.exception.EmailAlreadyExistsException;
import com.yagowill.springuserauth.exception.ResourceNotFoundException;
import com.yagowill.springuserauth.model.User;
import com.yagowill.springuserauth.model.UserRole;
import com.yagowill.springuserauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = User.builder()
                .id(userId)
                .name("Yago Will")
                .email("yago@email.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve retornar todos os usuários")
    void shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.findAll();

        assertEquals(1, result.size());
        assertEquals("Yago Will", result.get(0).getName());
    }

    @Test
    @DisplayName("Deve retornar usuário por ID")
    void shouldReturnUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse result = userService.findById(userId);

        assertNotNull(result);
        assertEquals("Yago Will", result.getName());
        assertEquals("yago@email.com", result.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar usuário inexistente")
    void shouldThrowExceptionWhenUserNotFound() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(randomId));
    }

    @Test
    @DisplayName("Deve atualizar usuário com sucesso")
    void shouldUpdateUser() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .name("Yago Atualizado")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse result = userService.update(userId, request);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com email duplicado")
    void shouldThrowExceptionWhenUpdatingWithExistingEmail() {
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("outro@email.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("outro@email.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> userService.update(userId, request));
    }

    @Test
    @DisplayName("Deve deletar usuário com sucesso")
    void shouldDeleteUser() {
        when(userRepository.existsById(userId)).thenReturn(true);
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.delete(userId));
        verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar usuário inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        UUID randomId = UUID.randomUUID();
        when(userRepository.existsById(randomId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(randomId));
        verify(userRepository, never()).deleteById(any());
    }
}
