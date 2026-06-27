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
    public void testAnalyzeJournal_EmptyOrNull() {
        JournalAnalysisResponse responseNull = aiSimulatorService.analyzeJournal(null, "JEE", "happy");
        assertThat(responseNull.stressLevel()).isEqualTo(10);
        assertThat(responseNull.triggers()).contains("No content");

        JournalAnalysisResponse responseBlank = aiSimulatorService.analyzeJournal("   ", "JEE", "happy");
        assertThat(responseBlank.stressLevel()).isEqualTo(10);
        assertThat(responseBlank.triggers()).contains("No content");
    }

    @Test
    public void testAnalyzeJournal_HighStressAndDistortions() {
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
    public void testAnalyzeJournal_FatigueTriggerMindfulness() {
        String journalText = "I am so tired. I cannot sleep and I feel completely exhausted.";
        JournalAnalysisResponse response = aiSimulatorService.analyzeJournal(journalText, "JEE", "fatigued");
        assertThat(response.triggers()).contains("Burnout & Fatigue");
        assertThat(response.mindfulnessExercise()).contains("Progressive Muscle Relaxation");
    }

    @Test
    public void testAnalyzeJournal_AllTriggersAndDistortions() {
        String content = "I had a mock test today, but my backlog is huge. My parents have high expectations. " +
                "I am so tired. What if I fail my career? This is the worst disaster ever. " +
                "I must get a perfect outcome or nothing. I should study. I feel hopeless and panic, very anxious and depressed.";

        JournalAnalysisResponse response = aiSimulatorService.analyzeJournal(content, "NEET", "hopeless");
        assertThat(response.stressLevel()).isEqualTo(100); // capped at 100
        assertThat(response.triggers()).contains(
            "Mock Test Performance",
            "Syllabus & Time Backlogs",
            "External & Peer Pressure",
            "Burnout & Fatigue",
            "Future & Career Uncertainty"
        );
        assertThat(response.cognitivePatterns()).contains(
            "Catastrophizing (Expecting the worst outcome)",
            "All-or-Nothing Thinking (Splitting logic)",
            "Should Statements (Unrealistic self-demands)"
        );
        assertThat(response.personalizedCopingStrategy()).contains("Immediate Action Required");
        assertThat(response.mindfulnessExercise()).contains("4-7-8 Deep Grounding Breathing");
    }

    @Test
    public void testGenerateCompanionResponse_EmptyMessage() {
        String responseNull = aiSimulatorService.generateCompanionResponse(null, "JEE", Collections.emptyList());
        assertThat(responseNull).contains("I am here for you");

        String responseBlank = aiSimulatorService.generateCompanionResponse("   ", "JEE", Collections.emptyList());
        assertThat(responseBlank).contains("I am here for you");
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

    @Test
    public void testGenerateCompanionResponse_Backlog() {
        String response = aiSimulatorService.generateCompanionResponse("I have a huge backlog in physics syllabus.", "JEE", Collections.emptyList());
        assertThat(response).contains("backlog");
        assertThat(response).contains("JEE");
    }

    @Test
    public void testGenerateCompanionResponse_HighStressHistory() {
        JournalEntry recent = new JournalEntry("stressed", "CAT", "Panicking.");
        recent.setStressLevel(85);

        String response = aiSimulatorService.generateCompanionResponse("Can you help me?", "CAT", List.of(recent));
        assertThat(response).contains("stress levels have been quite high");
        assertThat(response).contains("85%");
    }

    @Test
    public void testGenerateCompanionResponse_LowStressHistoryDefault() {
        JournalEntry recent = new JournalEntry("happy", "JEE", "Normal.");
        recent.setStressLevel(20);

        String response = aiSimulatorService.generateCompanionResponse("Can you help me?", "JEE", List.of(recent));
        assertThat(response).contains("putting in the work step-by-step");
        assertThat(response).contains("JEE");
    }
}
