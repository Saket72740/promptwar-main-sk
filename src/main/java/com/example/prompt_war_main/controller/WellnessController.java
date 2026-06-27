package com.example.prompt_war_main.controller;

import com.example.prompt_war_main.dto.JournalAnalysisResponse;
import com.example.prompt_war_main.model.ChatMessage;
import com.example.prompt_war_main.model.JournalEntry;
import com.example.prompt_war_main.repository.ChatMessageRepository;
import com.example.prompt_war_main.repository.JournalRepository;
import com.example.prompt_war_main.service.AISimulatorService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class WellnessController {

    private final JournalRepository journalRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AISimulatorService aiSimulatorService;

    @Autowired
    public WellnessController(JournalRepository journalRepository,
                              ChatMessageRepository chatMessageRepository,
                              AISimulatorService aiSimulatorService) {
        this.journalRepository = journalRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.aiSimulatorService = aiSimulatorService;
    }

    @GetMapping
    public String getDashboard(Model model, HttpSession session) {
        String exam = (String) session.getAttribute("selectedExam");
        if (exam == null) {
            exam = "JEE";
            session.setAttribute("selectedExam", exam);
        }

        // Limit fetched history to the last 10 entries for dashboard efficiency
        List<JournalEntry> journals = journalRepository.findTop10ByOrderByCreatedAtDesc();
        List<ChatMessage> chatMessages = chatMessageRepository.findAllByOrderByCreatedAtAsc();

        // Calculate average stress directly in database for efficiency
        Double averageStressVal = journalRepository.getAverageStressLevel();
        int averageStress = averageStressVal != null ? averageStressVal.intValue() : 0;
        long journalsCount = journalRepository.count();

        // Add model variables
        model.addAttribute("journals", journals);
        model.addAttribute("journalsCount", journalsCount);
        model.addAttribute("chatMessages", chatMessages);
        model.addAttribute("averageStress", averageStress);
        model.addAttribute("selectedExam", exam);
        model.addAttribute("newJournal", new JournalEntry());

        return "index";
    }

    @PostMapping("/journal")
    public String addJournalEntry(@RequestParam String mood,
                                  @RequestParam String exam,
                                  @RequestParam String content,
                                  HttpSession session) {
        if (mood == null || mood.isBlank() || exam == null || exam.isBlank() || content == null || content.isBlank()) {
            throw new IllegalArgumentException("All entry fields (Mood, Exam, Content) are required.");
        }

        session.setAttribute("selectedExam", exam);

        JournalAnalysisResponse analysis = aiSimulatorService.analyzeJournal(content, exam, mood);

        JournalEntry entry = new JournalEntry(mood, exam, content);
        entry.setStressLevel(analysis.stressLevel());
        entry.setTriggers(String.join(", ", analysis.triggers()));
        entry.setCognitivePatterns(String.join(", ", analysis.cognitivePatterns()));
        entry.setPersonalizedCopingStrategy(analysis.personalizedCopingStrategy());
        entry.setMindfulnessExercise(analysis.mindfulnessExercise());

        journalRepository.save(entry);

        return "redirect:/";
    }

    @PostMapping("/api/chat")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody Map<String, String> requestBody, HttpSession session) {
        String message = requestBody.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Message cannot be empty"));
        }

        String exam = (String) session.getAttribute("selectedExam");
        if (exam == null) {
            exam = "JEE";
        }

        ChatMessage userMsg = new ChatMessage("user", message, exam);
        chatMessageRepository.save(userMsg);

        // Fetch limited recent logs context (top 10 is fast and sufficient)
        List<JournalEntry> journals = journalRepository.findTop10ByOrderByCreatedAtDesc();

        String aiResponseText = aiSimulatorService.generateCompanionResponse(message, exam, journals);

        ChatMessage aiMsg = new ChatMessage("ai", aiResponseText, exam);
        chatMessageRepository.save(aiMsg);

        return ResponseEntity.ok(Map.of(
            "user", message,
            "ai", aiResponseText
        ));
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam String exam, HttpSession session) {
        if (exam == null || exam.isBlank()) {
            throw new IllegalArgumentException("Exam profile selection cannot be empty.");
        }
        session.setAttribute("selectedExam", exam);
        return "redirect:/";
    }

    @PostMapping("/clear")
    public String clearHistory() {
        journalRepository.deleteAll();
        chatMessageRepository.deleteAll();
        return "redirect:/";
    }
}
