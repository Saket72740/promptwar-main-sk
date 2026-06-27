package com.example.prompt_war_main.repository;

import com.example.prompt_war_main.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findAllByOrderByCreatedAtDesc();
}
