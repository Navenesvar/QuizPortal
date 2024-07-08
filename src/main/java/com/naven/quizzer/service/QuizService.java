package com.naven.quizzer.service;

import com.naven.quizzer.domain.QuizForm;
import com.naven.quizzer.repository.QuizRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizService {
    @Autowired
    private  QuizRepository quizRepository;

    public void saveQuiz(QuizForm quiz) {
        quizRepository.save(quiz);
    }

    public List<QuizForm> getQuizzesByUsername(String username) {
        return quizRepository.findByUsername(username);
    }

    public Optional<QuizForm> getQuizById(Long id) {
        return quizRepository.findById(id);
       }

    public List<QuizForm> getAllQuizzes() {
        return quizRepository.findAll();
    }

}

