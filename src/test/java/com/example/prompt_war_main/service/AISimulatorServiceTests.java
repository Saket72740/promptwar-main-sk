package com.example.prompt_war_main.service;

import com.example.prompt_war_main.dto.JournalAnalysisResponse;
import com.example.prompt_war_main.model.JournalEntry;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AISimulatorServiceTests {

    private final AISimulatorService aiSimulatorService = new AISimulatorService();

    @Test
    public void testAnalyzeJournal_HighStress() {
        String journalText = "Today was awful. I failed my chemistry mock test and parents have high expectations. I should study 16 hours. I feel overwhelmed and anxious.";
        JournalAnalysisResponse response = aiSimulatorService.analyzeJournal(journalText, "JEE", "anxious");

        assertThat(response.stressLevel()).isGreaterThan(60);
        assertThat(response.triggers()).contains("Mock Test Performance", "External & Peer Pressure");
        assertThat(response.cognitivePatterns()).contains("Should Statements (Unrealistic self-demands)");
        assertThat(response.personalizedCopingStrategy()).isNotEmpty();
        assertThat(response.mindfulnessExercise()).isNotEmpty();
    }

    @Test
    public void testAnalyzeJournal_LowStress() {
        String journalText = "A normal day. Solved physics problems.";
        JournalAnalysisResponse response = aiSimulatorService.analyzeJournal(journalText, "JEE", "happy");

        assertThat(response.stressLevel()).isLessThan(50);
        assertThat(response.triggers()).contains("General Academic Anxiety");
    }

    @Test
    public void testGenerateCompanionResponse_Tired() {
        String response = aiSimulatorService.generateCompanionResponse("I am so tired and exhausted from NEET prep", "NEET", Collections.emptyList());
        assertThat(response).contains("marathon, not a sprint");
        assertThat(response).contains("NEET");
    }

    @Test
    public void testGenerateCompanionResponse_Fail() {
        String response = aiSimulatorService.generateCompanionResponse("What if I fail UPSC?", "UPSC", Collections.emptyList());
        assertThat(response).contains("Fear of failure");
        assertThat(response).contains("UPSC");
    }

    @Test
    public void testGenerateCompanionResponse_MockTest() {
        String response = aiSimulatorService.generateCompanionResponse("I got bad marks in my mock test today.", "JEE", Collections.emptyList());
        assertThat(response).contains("mock test");
        assertThat(response).contains("JEE");
    }
}
