package com.example.prompt_war_main.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_entries")
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private String mood;

    @Column(nullable = false)
    private String exam;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int stressLevel;

    @Column(length = 500)
    private String triggers;

    @Column(length = 500)
    private String cognitivePatterns;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String personalizedCopingStrategy;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String mindfulnessExercise;

    // Constructors
    public JournalEntry() {
        this.createdAt = LocalDateTime.now();
    }

    public JournalEntry(String mood, String exam, String content) {
        this();
        this.mood = mood;
        this.exam = exam;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getExam() {
        return exam;
    }

    public void setExam(String exam) {
        this.exam = exam;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(int stressLevel) {
        this.stressLevel = stressLevel;
    }

    public String getTriggers() {
        return triggers;
    }

    public void setTriggers(String triggers) {
        this.triggers = triggers;
    }

    public String getCognitivePatterns() {
        return cognitivePatterns;
    }

    public void setCognitivePatterns(String cognitivePatterns) {
        this.cognitivePatterns = cognitivePatterns;
    }

    public String getPersonalizedCopingStrategy() {
        return personalizedCopingStrategy;
    }

    public void setPersonalizedCopingStrategy(String personalizedCopingStrategy) {
        this.personalizedCopingStrategy = personalizedCopingStrategy;
    }

    public String getMindfulnessExercise() {
        return mindfulnessExercise;
    }

    public void setMindfulnessExercise(String mindfulnessExercise) {
        this.mindfulnessExercise = mindfulnessExercise;
    }
}
