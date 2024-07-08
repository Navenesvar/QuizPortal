package com.naven.quizzer.repository;

import com.naven.quizzer.domain.QuizForm;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<QuizForm, Long> {
        @SuppressWarnings("unchecked")
        QuizForm save(QuizForm quiz);
        List<QuizForm> findByUsername(String username);
        Optional<QuizForm> findById(Long id);
        List<QuizForm> findAll();
        Long findDurationById(Long quizId);
}
