package com.example.prompt_war_main.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

public class EntityTests {

    @Test
    public void testJournalEntryAccessors() {
        JournalEntry entry = new JournalEntry();
        entry.setId(10L);
        LocalDateTime now = LocalDateTime.now();
        entry.setCreatedAt(now);
        entry.setMood("happy");
        entry.setExam("JEE");
        entry.setContent("Studied physics.");
        entry.setStressLevel(20);
        entry.setTriggers("None");
        entry.setCognitivePatterns("None");
        entry.setPersonalizedCopingStrategy("Keep studying.");
        entry.setMindfulnessExercise("Breath normally.");

        assertThat(entry.getId()).isEqualTo(10L);
        assertThat(entry.getCreatedAt()).isEqualTo(now);
        assertThat(entry.getMood()).isEqualTo("happy");
        assertThat(entry.getExam()).isEqualTo("JEE");
        assertThat(entry.getContent()).isEqualTo("Studied physics.");
        assertThat(entry.getStressLevel()).isEqualTo(20);
        assertThat(entry.getTriggers()).isEqualTo("None");
        assertThat(entry.getCognitivePatterns()).isEqualTo("None");
        assertThat(entry.getPersonalizedCopingStrategy()).isEqualTo("Keep studying.");
        assertThat(entry.getMindfulnessExercise()).isEqualTo("Breath normally.");

        // Test parameterized constructor
        JournalEntry entry2 = new JournalEntry("anxious", "NEET", "Confused.");
        assertThat(entry2.getMood()).isEqualTo("anxious");
        assertThat(entry2.getExam()).isEqualTo("NEET");
        assertThat(entry2.getContent()).isEqualTo("Confused.");
        assertThat(entry2.getCreatedAt()).isNotNull();
    }

    @Test
    public void testChatMessageAccessors() {
        ChatMessage message = new ChatMessage();
        message.setId(5L);
        LocalDateTime now = LocalDateTime.now();
        message.setCreatedAt(now);
        message.setSender("ai");
        message.setContent("Hello, I am here.");
        message.setExam("GATE");

        assertThat(message.getId()).isEqualTo(5L);
        assertThat(message.getCreatedAt()).isEqualTo(now);
        assertThat(message.getSender()).isEqualTo("ai");
        assertThat(message.getContent()).isEqualTo("Hello, I am here.");
        assertThat(message.getExam()).isEqualTo("GATE");

        // Test parameterized constructor
        ChatMessage message2 = new ChatMessage("user", "Hello", "CAT");
        assertThat(message2.getSender()).isEqualTo("user");
        assertThat(message2.getContent()).isEqualTo("Hello");
        assertThat(message2.getExam()).isEqualTo("CAT");
        assertThat(message2.getCreatedAt()).isNotNull();
    }
}
