package com.naven.quizzer.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.naven.quizzer.domain.Question;
import com.naven.quizzer.repository.QuestionRepository;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public void saveQuestion(Question question) {
        questionRepository.save(question);
    }

    public List<Question> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    public String getCorrectAnswer(Long questionId) {
        Optional<Question> question = questionRepository.findById(questionId);
        return question.isPresent() ? question.get().getCorrect() : null;
    }
}
