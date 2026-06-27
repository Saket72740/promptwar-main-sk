package com.example.prompt_war_main.service;

import com.example.prompt_war_main.dto.JournalAnalysisResponse;
import com.example.prompt_war_main.model.JournalEntry;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AISimulatorService {

    private static final Map<String, String[]> TRIGGER_KEYWORDS;
    static {
        Map<String, String[]> map = new LinkedHashMap<>();
        map.put("Mock Test Performance", new String[]{"mock", "test", "score", "marks", "rank"});
        map.put("Syllabus & Time Backlogs", new String[]{"backlog", "time", "syllabus", "schedule", "hours", "revision"});
        map.put("External & Peer Pressure", new String[]{"parent", "mother", "father", "expect", "compare", "peer", "friend"});
        map.put("Burnout & Fatigue", new String[]{"tired", "sleep", "exhaust", "heavy", "drain"});
        map.put("Future & Career Uncertainty", new String[]{"fail", "career", "future", "drop", "give up", "quit"});
        TRIGGER_KEYWORDS = Collections.unmodifiableMap(map);
    }

    private static final Map<String, String[]> DISTORTION_KEYWORDS;
    static {
        Map<String, String[]> map = new LinkedHashMap<>();
        map.put("Catastrophizing (Expecting the worst outcome)", new String[]{"never", "ruin", "over", "worst", "disaster"});
        map.put("All-or-Nothing Thinking (Splitting logic)", new String[]{"perfect", "nothing", "useless", "either", "zero"});
        map.put("Should Statements (Unrealistic self-demands)", new String[]{"should", "must", "ought", "have to"});
        DISTORTION_KEYWORDS = Collections.unmodifiableMap(map);
    }

    private static final String[] HIGH_STRESS_KEYWORDS = {"depressed", "anxious", "scared", "crying", "suffocating", "hate", "hopeless", "panic"};

    public JournalAnalysisResponse analyzeJournal(String content, String exam, String mood) {
        if (content == null || content.isBlank()) {
            return new JournalAnalysisResponse(
                10,
                List.of("No content"),
                List.of("None"),
                "Write down your thoughts to receive personalized coping strategies.",
                "Take 3 deep, slow breaths."
            );
        }

        String lowerContent = content.toLowerCase();
        
        List<String> triggers = detectKeywords(lowerContent, TRIGGER_KEYWORDS, "General Academic Anxiety");
        List<String> distortions = detectKeywords(lowerContent, DISTORTION_KEYWORDS, "None detected (Healthy perspective)");
        int stressLevel = calculateStressScore(lowerContent, mood, triggers.size(), distortions.size(), distortions.get(0).startsWith("None"));
        String copingStrategy = generateCopingStrategy(triggers, exam, stressLevel);
        String mindfulnessExercise = generateMindfulnessExercise(stressLevel, triggers);

        return new JournalAnalysisResponse(stressLevel, triggers, distortions, copingStrategy, mindfulnessExercise);
    }

    private List<String> detectKeywords(String content, Map<String, String[]> keywordMap, String defaultItem) {
        List<String> detected = new ArrayList<>();
        for (Map.Entry<String, String[]> entry : keywordMap.entrySet()) {
            for (String kw : entry.getValue()) {
                if (content.contains(kw)) {
                    detected.add(entry.getKey());
                    break; // Once a category is matched, move to next category
                }
            }
        }
        if (detected.isEmpty()) {
            detected.add(defaultItem);
        }
        return detected;
    }

    private int calculateStressScore(String content, String mood, int triggerCount, int distortionCount, boolean noDistortions) {
        int baseStress = 25;
        if (mood != null) {
            switch (mood.toLowerCase()) {
                case "happy": baseStress = 10; break;
                case "anxious": baseStress = 60; break;
                case "fatigued": baseStress = 50; break;
                case "stressed": baseStress = 80; break;
                case "hopeless": baseStress = 90; break;
                default: baseStress = 25; break;
            }
        }

        baseStress += triggerCount * 12;

        if (!noDistortions) {
            baseStress += distortionCount * 10;
        }

        for (String kw : HIGH_STRESS_KEYWORDS) {
            if (content.contains(kw)) {
                baseStress += 8;
            }
        }

        return Math.min(100, Math.max(10, baseStress));
    }


    private String generateCopingStrategy(List<String> triggers, String exam, int stressLevel) {
        StringBuilder strategy = new StringBuilder();
        
        strategy.append("### Your Personalized Coping Plan (Target: ").append(exam).append(")\n\n");
        
        if (stressLevel > 75) {
            strategy.append("**Immediate Action Required:** Your stress level is elevated (").append(stressLevel).append("%). Before resuming study, take a complete 30-minute cognitive offload. No screens, no books.\n\n");
        }

        for (String trigger : triggers) {
            switch (trigger) {
                case "Mock Test Performance" -> {
                    strategy.append("- **Reframe Test Analytics:** Do not equate a low mock score to final failure. For your ").append(exam).append(" preparation, treat mocks as diagnostic tools rather than prediction indicators. Dedicate 2 hours to mapping out the topics where errors occurred, noting down the concepts to revise, rather than brooding over the percentage/rank.\n");
                }
                case "Syllabus & Time Backlogs" -> {
                    strategy.append("- **Syllabus Triage (Rule of 3):** With backlogs in your ").append(exam).append(" syllabus, pick exactly 3 core topics per week to catch up on. Use the Pomodoro Technique (25 min study, 5 min break) to avoid feeling overwhelmed. Accept that you do not need 100% completion to excel; focus on high-yield sections first.\n");
                }
                case "External & Peer Pressure" -> {
                    strategy.append("- **External Compartmentalization:** Establish boundaries. Limit study-related discussions with friends or relatives who induce comparison. Remind yourself: 'My journey for ").append(exam).append(" is unique to my pace, and comparisons are fundamentally invalid.'\n");
                }
                case "Burnout & Fatigue" -> {
                    strategy.append("- **Circadian Restoration:** You are experiencing physical exhaustion. Set a firm sleep deadline. Studying at 40% efficiency for 14 hours is far less effective than studying at 90% efficiency for 8 hours. Prioritize at least 7 hours of sleep tonight.\n");
                }
                case "Future & Career Uncertainty" -> {
                    strategy.append("- **Focus on Process, Not Outcome:** Shift focus to the immediate next hour. Break down your career fear by designing a simple 'Plan B' path in your mind so that the high-stakes pressure of ").append(exam).append(" is minimized. Focus exclusively on executing today's study goal.\n");
                }
                default -> {
                    strategy.append("- **Daily Anchor Routine:** Spend 15 minutes each morning planning 2 key deliverables. Build confidence by completing small, manageable milestones.\n");
                }
            }
        }
        
        return strategy.toString();
    }

    private String generateMindfulnessExercise(int stressLevel, List<String> triggers) {
        if (stressLevel > 70) {
            return """
                ### 4-7-8 Deep Grounding Breathing (Adaptive Exercise)
                *Best suited for high stress levels.*
                
                1. Sit comfortably with your back straight.
                2. Exhale completely through your mouth, making a whoosh sound.
                3. Close your mouth and inhale quietly through your nose to a mental count of **4**.
                4. Hold your breath for a count of **7**.
                5. Exhale completely through your mouth, making a whoosh sound to a count of **8**.
                6. Repeat this cycle **4 times** to instantly slow down your heart rate and cortisol levels.
                """;
        } else if (triggers.contains("Burnout & Fatigue")) {
            return """
                ### The Progressive Muscle Relaxation (PMR) Technique
                *Best suited for fatigue and body tension.*
                
                1. Lie down comfortably and close your eyes.
                2. Starting at your toes, tense the muscles as hard as you can for 5 seconds, then release completely.
                3. Move up to your calves, thighs, stomach, hands, shoulders, and jaw.
                4. Notice the contrast between tension and complete relaxation.
                5. Perform this for 5-10 minutes to trigger physical rest recovery.
                """;
        } else {
            return """
                ### The 5-4-3-2-1 Cognitive Grounding Exercise
                *Best suited for focus and overthinking.*
                
                Look around your room and identify:
                - **5** things you can see (e.g., your book, your pen, a desk light).
                - **4** things you can touch or feel (e.g., the texture of your chair, your feet on the floor).
                - **3** things you can hear (e.g., the fan, outside traffic, your breathing).
                - **2** things you can smell (e.g., pages of a book, tea/coffee).
                - **1** thing you can taste or positive thought about yourself.
                """;
        }
    }

    public String generateCompanionResponse(String userMessage, String exam, List<JournalEntry> history) {
        if (userMessage == null || userMessage.isBlank()) {
            return "I am here for you. How is your study prep going today?";
        }

        String lowerMsg = userMessage.toLowerCase();
        int recentStress = 30;
        String recentMood = "Unknown";
        if (history != null && !history.isEmpty()) {
            JournalEntry recent = history.get(0);
            recentStress = recent.getStressLevel();
            recentMood = recent.getMood();
        }

        // Adaptive response generation based on message keywords
        if (lowerMsg.contains("tired") || lowerMsg.contains("burnout") || lowerMsg.contains("exhausted") || lowerMsg.contains("sleep")) {
            return String.format(Locale.US,
                "I hear you, and it is completely understandable to feel tired. Preparing for %s is a marathon, not a sprint. " +
                "Your recent stress indicator is at %d%%. Please don't force yourself to study through extreme exhaustion tonight. " +
                "Could you try stepping away from your books for a few hours and letting your mind rest? What is one relaxing thing you can do right now?",
                exam, recentStress);
        }

        if (lowerMsg.contains("fail") || lowerMsg.contains("not clear") || lowerMsg.contains("scared") || lowerMsg.contains("fear")) {
            return String.format(Locale.US,
                "Fear of failure is very real, especially for an competitive exam like %s. It's a sign of how much this matters to you. " +
                "But remember: this exam is a test of your speed and conceptual memory, NOT a test of your intelligence, capability, or future value as a person. " +
                "Let's focus on what we can control today. What is a small task you can tackle right now to rebuild your confidence?",
                exam);
        }

        if (lowerMsg.contains("mock") || lowerMsg.contains("test") || lowerMsg.contains("marks") || lowerMsg.contains("score")) {
            return String.format(Locale.US,
                "Ah, mock test scores. They can be such a mood-killer, can't they? But in the syllabus context of %s, mock tests are designed to expose weak points *before* the actual exam day. " +
                "Every mistake you made in that test is a gift—it's one less mistake you will make in the real exam. " +
                "Instead of looking at the total score, what if we pick just 3 questions you got wrong and study their solutions? I can help you stay focused.",
                exam);
        }

        if (lowerMsg.contains("backlog") || lowerMsg.contains("syllabus") || lowerMsg.contains("behind") || lowerMsg.contains("late")) {
            return String.format(Locale.US,
                "Backlogs are the most common source of stress for %s students. The feeling that you're lagging behind can be paralyzing. " +
                "But trying to cover everything in a panic will only lead to shallow learning. " +
                "Let's make a deal: write down a list of your backlogs, prioritize them by high-weightage topics, and schedule just 45 minutes a day for catching up. " +
                "Let the rest of your study plan go normally. How does that sound?",
                exam);
        }

        // Fallback default empathetic responses
        if (recentStress > 70) {
            return String.format(Locale.US,
                "I noticed your stress levels have been quite high recently (%d%%, expressing '%s'). " +
                "Remember, I'm here as your empathetic companion. You don't have to carry this entire burden alone. " +
                "How are you holding up physically? Have you had a glass of water and some fresh air today?",
                recentStress, recentMood);
        }

        return String.format(Locale.US,
            "I'm glad we are connected. Preparing for %s is tough, but you are putting in the work step-by-step. " +
            "How can I support you today? We can break down a study schedule, do a relaxation exercise, or just talk about how you're feeling.",
            exam);
    }
}
