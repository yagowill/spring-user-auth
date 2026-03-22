package com.yagowill.springuserauth.service;

import com.yagowill.springuserauth.dto.AuthResponse;
import com.yagowill.springuserauth.dto.LoginRequest;
import com.yagowill.springuserauth.dto.RegisterRequest;
import com.yagowill.springuserauth.exception.EmailAlreadyExistsException;
import com.yagowill.springuserauth.model.User;
import com.yagowill.springuserauth.model.UserRole;
import com.yagowill.springuserauth.repository.UserRepository;
import com.yagowill.springuserauth.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Deve registrar um novo usuário com sucesso")
    void shouldRegisterNewUser() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Yago Will")
                .email("yago@email.com")
                .password("123456")
                .build();

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .name("Yago Will")
                .email("yago@email.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(userRepository.existsByEmail("yago@email.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(86400000L);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals(86400000L, response.getExpiresIn());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar com email duplicado")
    void shouldThrowExceptionWhenEmailExists() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Yago Will")
                .email("yago@email.com")
                .password("123456")
                .build();

        when(userRepository.existsByEmail("yago@email.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Deve fazer login com sucesso")
    void shouldLoginSuccessfully() {
        LoginRequest request = LoginRequest.builder()
                .email("yago@email.com")
                .password("123456")
                .build();

        User user = User.builder()
                .id(UUID.randomUUID())
                .name("Yago Will")
                .email("yago@email.com")
                .password("encodedPassword")
                .role(UserRole.USER)
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user, null));
        when(userRepository.findByEmail("yago@email.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(86400000L);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any());
    }
}
