package com.naven.quizzer.service;

import java.util.List;
import java.util.Optional;
import com.naven.quizzer.domain.QuizForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.naven.quizzer.domain.History;
import com.naven.quizzer.repository.HistoryRepository;
import com.naven.quizzer.repository.QuizRepository;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;
    @Autowired
    private  QuizRepository quizRepository;

    public void saveHistory(History history) {
        historyRepository.save(history);
    }

    public History getLatestHistory(Long quizId) {
        return historyRepository.findTopByQuizIdOrderByTimestampDesc(quizId);
    }

    public Optional<History> findHistoryByUsernameAndQuizId(String username, Long quizId) {
        return historyRepository.findByUsernameAndQuizId(username, quizId);
    }
    public List<History> findHistoriesByUsername(String username) {
        return historyRepository.findByUsername(username);
    }
    public void setQuizTitle(History history, Long quizId) {
        Optional<QuizForm> quizFormOptional = quizRepository.findById(quizId);
        
        if (quizFormOptional.isPresent()) {
            QuizForm quizForm = quizFormOptional.get();
            history.setQuizTitle(quizForm.getTitle());
        }
    }
}
