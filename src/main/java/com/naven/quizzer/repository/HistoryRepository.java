package com.naven.quizzer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.naven.quizzer.domain.History;

public interface HistoryRepository extends JpaRepository<History, Long> {
    History findTopByQuizIdOrderByTimestampDesc(Long quizId);
    @SuppressWarnings("unchecked")
    History save(History history);
    Optional<History> findByUsernameAndQuizId(String username, Long quizId);
    List<History> findByUsername(String username);
}
