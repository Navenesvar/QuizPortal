package com.naven.quizzer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.naven.quizzer.domain.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    @SuppressWarnings("unchecked")
    Question save(Question question);
    List<Question> findByQuizId(Long quizId);
}

