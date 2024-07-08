package com.naven.quizzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.naven.quizzer.domain.History;
import com.naven.quizzer.domain.Question;
import com.naven.quizzer.domain.QuizForm;
import com.naven.quizzer.service.HistoryService;
import com.naven.quizzer.service.QuestionService;
import com.naven.quizzer.service.QuizService;

import jakarta.servlet.http.HttpSession;

@Controller
public class QuizController {

    @Autowired
    private QuizService quizService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private HistoryService historyService;

    @GetMapping("/createQuiz")
    public String showCreateQuizForm(Model model, @RequestParam("username") String username) {
        List<QuizForm> quizzes = quizService.getQuizzesByUsername(username);
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("quizform", new QuizForm());
        model.addAttribute("username", username);
        return "createQuiz";
    }

    @GetMapping("/quizform")
    public String showQuizForm(Model model, @RequestParam("username") String username) {
        model.addAttribute("quizform", new QuizForm());
        model.addAttribute("username", username);
        return "quizform";
    }

    @PostMapping("/quizform")
    public String quizForm(@RequestParam("title") String quizTitle,
                           @RequestParam("description") String quizDescription,
                           @RequestParam("username") String username,
                           Model model) {
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            QuizForm quiz = new QuizForm();
            quiz.setTitle(quizTitle);
            quiz.setDescription(quizDescription);
            quiz.setUsername(username);
            quiz.setCreatedAt(timestamp);
            quizService.saveQuiz(quiz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/createQuiz?username=" + username;
    }

    @GetMapping("/quiz/update")
    public String showUpdateQuizForm(@RequestParam("id") Long id, @RequestParam("username") String username, Model model) {
        Optional<QuizForm> quiz = quizService.getQuizById(id);
        List<Question> quest = questionService.getQuestionsByQuizId(id);

        if (quiz.isPresent()) {
            model.addAttribute("quiz", quiz.get());
            model.addAttribute("username", username);
            model.addAttribute("questions", quest);
            return "update";
        } else {
            return "redirect:/createQuiz?username=" + username;
        }
    }

    @PostMapping("/quiz/update")
    public String updateQuizDetails(@ModelAttribute("quiz") QuizForm updatedQuiz,
                                    @RequestParam("username") String username) {
        quizService.saveQuiz(updatedQuiz);
        return "redirect:/createQuiz?username=" + username;
    }

    @PostMapping("/addquestion")
    public String addQuestion(@RequestParam("quizId") Long quizId, @RequestParam("question") String questionText,
                              @RequestParam("optionA") String optionA, @RequestParam("optionB") String optionB,
                              @RequestParam("optionC") String optionC, @RequestParam("optionD") String optionD,
                              @RequestParam("correct") String correct, @RequestParam("username") String username) {
        Question question = new Question();
        question.setQuizId(quizId);
        question.setQuestion(questionText);
        question.setOptionA(optionA);
        question.setOptionB(optionB);
        question.setOptionC(optionC);
        question.setOptionD(optionD);
        question.setCorrect(correct);
        questionService.saveQuestion(question);
        return "redirect:/quiz/update?id=" + quizId + "&username=" + username;
    }

    @GetMapping("/takeQuiz")
    public String showTakeQuizPage(@RequestParam("username") String username, Model model) {
        List<QuizForm> quizzes = quizService.getAllQuizzes();
        model.addAttribute("quizzes", quizzes);
        model.addAttribute("username", username);
        return "takeQuiz";
    }

    @GetMapping("/startQuiz")
    public String startQuiz(@RequestParam("id") Long quizId, @RequestParam("username") String username, Model model, HttpSession session) {
        List<Question> questions = questionService.getQuestionsByQuizId(quizId);
        Optional<QuizForm> quiz = quizService.getQuizById(quizId);
        QuizForm dura;
        if (quiz.isPresent()) {
            dura = quiz.get();
            session.setAttribute("quizDuration", dura.getDuration());
            Long quizDurationMinutes = dura.getDuration();
            model.addAttribute("quizDuration", quizDurationMinutes);
        }
        if (questions.isEmpty()) {
            return "comingSoon";
        }
        session.setAttribute("quizId", quizId);
        session.setAttribute("questions", questions);
        session.setAttribute("currentQuestionIndex", 0);
        session.setAttribute("score", 0);
        session.setAttribute("username", username);
        model.addAttribute("quizId", quizId);
        model.addAttribute("currentQuestion", questions.get(0));
        model.addAttribute("value", "Next");
        return "startQuiz";
    }

    @PostMapping("/submitAnswer")
    public String submitAnswer(@RequestParam("quizId") Long quizId,
                               @RequestParam("questionId") Long questionId,
                               @RequestParam("selectedOption") String selectedOption,
                               @RequestParam("action") String action,
                               HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        List<Question> questions = (List<Question>) session.getAttribute("questions");
        int currentQuestionIndex = (int) session.getAttribute("currentQuestionIndex");

        String correctAnswer = questionService.getCorrectAnswer(questionId);
        int score = (int) session.getAttribute("score");

        if ("Next".equals(action)) {
            if (selectedOption.equals(correctAnswer)) {
                score++;
            }
            session.setAttribute("score", score);
            currentQuestionIndex++;
        } else if ("Back".equals(action)) {
            currentQuestionIndex--;
            if (selectedOption.equals(correctAnswer)) {
                score--;
            }
        }

        session.setAttribute("currentQuestionIndex", currentQuestionIndex);

        if (currentQuestionIndex < questions.size()) {
            model.addAttribute("quizId", quizId);
            model.addAttribute("currentQuestion", questions.get(currentQuestionIndex));
            model.addAttribute("selectedOption", selectedOption);
            model.addAttribute("currentQuestionIndex", currentQuestionIndex);
            model.addAttribute("value", ((currentQuestionIndex == (questions.size() - 1)) ? "Submit" : "Next"));
            return "startQuiz";
        } else {
            int totalQuestions = questions.size();
            int passingMarks = totalQuestions / 2;
            String verdict = score >= passingMarks ? "Pass" : "Fail";
            String username = (String) session.getAttribute("username");

            Optional<History> existingHistoryOpt = historyService.findHistoryByUsernameAndQuizId(username, quizId);
            History history;

            if (existingHistoryOpt.isPresent()) {
                history = existingHistoryOpt.get();
                if (score > history.getScore()) {
                    history.setScore(score);
                    history.setVerdict(verdict);
                }
                history.setTimestamp(LocalDateTime.now());
            } else {
                history = new History();
                history.setQuizId(quizId);
                historyService.setQuizTitle(history, history.getQuizId());
                history.setUsername(username);
                history.setTimestamp(LocalDateTime.now());
                history.setTotalMarks(totalQuestions);
                history.setPassingMarks(passingMarks);
                history.setScore(score);
                history.setVerdict(verdict);
            }

            historyService.saveHistory(history);

            model.addAttribute("username", username);
            model.addAttribute("score", history.getScore());
            model.addAttribute("totalQuestions", totalQuestions);
            model.addAttribute("passingMarks", passingMarks);
            model.addAttribute("verdict", history.getVerdict());
            model.addAttribute("timestamp", history.getTimestamp());

            return "quizFinished";
        }
    }

    @GetMapping("/reports")
    public String showReportsPage(@RequestParam("username") String username, Model model) {
        List<History> historyList = historyService.findHistoriesByUsername(username);
        model.addAttribute("historyList", historyList);
        model.addAttribute("username", username);
        return "reports";
    }
}
