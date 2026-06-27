package com.example.prompt_war_main.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WellnessControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetDashboard_Default() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("journals"))
                .andExpect(model().attributeExists("chatMessages"))
                .andExpect(model().attribute("selectedExam", "JEE"));
    }

    @Test
    public void testAddJournalEntry_Success() throws Exception {
        mockMvc.perform(post("/journal")
                        .param("mood", "stressed")
                        .param("exam", "NEET")
                        .param("content", "I am feeling extremely nervous about mock biology questions."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testAddJournalEntry_ValidationFailure() throws Exception {
        // Missing fields should throw IllegalArgumentException and route to error page via Exception Handler
        mockMvc.perform(post("/journal")
                        .param("mood", "")
                        .param("exam", "JEE")
                        .param("content", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void testSendMessage_Success() throws Exception {
        // First log a journal to establish context
        mockMvc.perform(post("/journal")
                        .param("mood", "fatigued")
                        .param("exam", "CAT")
                        .param("content", "I am very tired from reviewing quantitative aptitude for 12 hours straight."));

        // Test sending message
        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"I feel exhausted today.\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value("I feel exhausted today."))
                .andExpect(jsonPath("$.ai").exists());
    }

    @Test
    public void testSendMessage_EmptyMessageValidationFailure() throws Exception {
        // Empty message should return 400 Bad Request
        mockMvc.perform(post("/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Message cannot be empty"));
    }

    @Test
    public void testUpdateProfile_Success() throws Exception {
        mockMvc.perform(post("/profile")
                        .param("exam", "UPSC"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void testUpdateProfile_ValidationFailure() throws Exception {
        mockMvc.perform(post("/profile")
                        .param("exam", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    public void testClearHistory_Success() throws Exception {
        mockMvc.perform(post("/clear"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
