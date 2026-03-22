package com.yagowill.springuserauth.service;

import com.yagowill.springuserauth.dto.AuthResponse;
import com.yagowill.springuserauth.dto.LoginRequest;
import com.yagowill.springuserauth.dto.RegisterRequest;
import com.yagowill.springuserauth.exception.EmailAlreadyExistsException;
import com.yagowill.springuserauth.model.User;
import com.yagowill.springuserauth.model.UserRole;
import com.yagowill.springuserauth.repository.UserRepository;
import com.yagowill.springuserauth.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email já cadastrado: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.USER)
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtService.getExpirationTime())
                .build();
    }
}
