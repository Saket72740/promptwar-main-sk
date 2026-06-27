package com.example.prompt_war_main.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetToken_Success() throws Exception {
        // "admin:admin" base64 encoded is "YWRtaW46YWRtaW4="
        mockMvc.perform(get("/api/token")
                        .header("Authorization", "Basic YWRtaW46YWRtaW4="))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value("3600"));
    }

    @Test
    public void testGetToken_InvalidCredentials() throws Exception {
        // "admin:wrong" base64 encoded is "YWRtaW46d3Jvbmc="
        mockMvc.perform(get("/api/token")
                        .header("Authorization", "Basic YWRtaW46d3Jvbmc="))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }

    @Test
    public void testGetToken_MissingHeader() throws Exception {
        mockMvc.perform(get("/api/token"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("WWW-Authenticate", "Basic realm=\"API\""))
                .andExpect(jsonPath("$.error").value("Basic authentication is required"));
    }

    @Test
    public void testGetToken_MalformedHeader() throws Exception {
        mockMvc.perform(get("/api/token")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Basic authentication is required"));
    }

    @Test
    public void testGetToken_MalformedBase64() throws Exception {
        mockMvc.perform(get("/api/token")
                        .header("Authorization", "Basic invalid@@@base64"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Invalid username or password"));
    }
}
