package com.example.prompt_war_main.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTests {

    private JwtService jwtService;

    @BeforeEach
    public void setup() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", "my-super-secret-key-that-is-at-least-32-bytes-long");
    }

    @Test
    public void testConstructor() {
        assertThat(jwtService).isNotNull();
    }

    @Test
    public void testTokenGenerationAndValidation() {
        String token = jwtService.generateToken("admin");
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);

        boolean isValid = jwtService.validateToken(token);
        assertThat(isValid).isTrue();
    }

    @Test
    public void testTokenValidation_Expired() {
        String token = jwtService.generateTokenWithExpiry("admin", -10);
        boolean isValid = jwtService.validateToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    public void testTokenValidation_InvalidInputs() {
        assertThat(jwtService.validateToken(null)).isFalse();
        assertThat(jwtService.validateToken("")).isFalse();
        assertThat(jwtService.validateToken("invalidTokenFormat")).isFalse();
        assertThat(jwtService.validateToken("header.payload.signature_mismatch")).isFalse();
        assertThat(jwtService.validateToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid@@@base64.signature")).isFalse();
    }
}
