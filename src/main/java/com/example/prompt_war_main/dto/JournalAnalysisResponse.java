package com.example.prompt_war_main.dto;

import java.util.List;

public record JournalAnalysisResponse(
    int stressLevel,
    List<String> triggers,
    List<String> cognitivePatterns,
    String personalizedCopingStrategy,
    String mindfulnessExercise
) {}
