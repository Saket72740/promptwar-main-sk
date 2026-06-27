package com.example.prompt_war_main.repository;

import com.example.prompt_war_main.model.ChatMessage;
import com.example.prompt_war_main.model.JournalEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RepositoryTests {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Test
    public void testSaveAndFindJournalEntries() {
        JournalEntry entry = new JournalEntry("anxious", "JEE", "I am worried about advanced mathematics mock exams.");
        entry.setStressLevel(65);
        entry.setTriggers("Mock Test Performance");
        entry.setCognitivePatterns("Catastrophizing");
        entry.setPersonalizedCopingStrategy("Reframe mathematics topics.");
        entry.setMindfulnessExercise("4-7-8 Breathing");

        JournalEntry saved = journalRepository.save(entry);
        assertThat(saved.getId()).isNotNull();

        List<JournalEntry> journals = journalRepository.findAllByOrderByCreatedAtDesc();
        assertThat(journals).hasSize(1);
        assertThat(journals.get(0).getMood()).isEqualTo("anxious");
        assertThat(journals.get(0).getStressLevel()).isEqualTo(65);
    }

    @Test
    public void testSaveAndFindChatMessages() {
        ChatMessage message = new ChatMessage("user", "Hello companion, I am tired.", "NEET");
        ChatMessage saved = chatMessageRepository.save(message);
        assertThat(saved.getId()).isNotNull();

        List<ChatMessage> history = chatMessageRepository.findAllByOrderByCreatedAtAsc();
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getSender()).isEqualTo("user");
        assertThat(history.get(0).getExam()).isEqualTo("NEET");
    }
}
