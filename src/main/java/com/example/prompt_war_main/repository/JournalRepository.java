package com.example.prompt_war_main.repository;

import com.example.prompt_war_main.model.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JournalRepository extends JpaRepository<JournalEntry, Long> {
    List<JournalEntry> findAllByOrderByCreatedAtDesc();
    List<JournalEntry> findTop10ByOrderByCreatedAtDesc();

    @Query("SELECT AVG(j.stressLevel) FROM JournalEntry j")
    Double getAverageStressLevel();
}
