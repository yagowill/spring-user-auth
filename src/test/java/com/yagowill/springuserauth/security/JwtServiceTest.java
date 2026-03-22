package com.yagowill.springuserauth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "dGVzdFNlY3JldEtleVRoYXRJc0F0TGVhc3QyNTZCaXRzTG9uZ0ZvckhTMjU2QWxnb3JpdGhtVGVzdGluZzIwMjQh";
    private static final long EXPIRATION = 86400000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);
    }

    private UserDetails createUserDetails(String email) {
        return new User(email, "password", Collections.emptyList());
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido")
    void shouldGenerateValidToken() {
        UserDetails userDetails = createUserDetails("test@email.com");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve extrair o username do token")
    void shouldExtractUsername() {
        UserDetails userDetails = createUserDetails("test@email.com");
        String token = jwtService.generateToken(userDetails);

        String username = jwtService.extractUsername(token);

        assertEquals("test@email.com", username);
    }

    @Test
    @DisplayName("Deve validar um token válido")
    void shouldValidateValidToken() {
        UserDetails userDetails = createUserDetails("test@email.com");
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Deve invalidar token com username diferente")
    void shouldInvalidateTokenWithDifferentUsername() {
        UserDetails userDetails = createUserDetails("test@email.com");
        UserDetails otherUser = createUserDetails("other@email.com");
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, otherUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Deve invalidar token expirado")
    void shouldInvalidateExpiredToken() {
        UserDetails userDetails = createUserDetails("test@email.com");

        String expiredToken = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis() - 200000))
                .expiration(new Date(System.currentTimeMillis() - 100000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)), Jwts.SIG.HS256)
                .compact();

        assertThrows(Exception.class, () -> jwtService.isTokenValid(expiredToken, userDetails));
    }

    @Test
    @DisplayName("Deve retornar tempo de expiração correto")
    void shouldReturnCorrectExpirationTime() {
        assertEquals(EXPIRATION, jwtService.getExpirationTime());
    }
}
