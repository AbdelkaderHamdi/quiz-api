package com.demo.quiz.service;

import com.demo.quiz.data.QuizRepository;
import com.demo.quiz.model.Answer;
import com.demo.quiz.model.Question;
import com.demo.quiz.model.Quiz;
import com.demo.quiz.model.QuizSummary;
import com.demo.quiz.model.Submission;
import com.demo.quiz.model.SubmitRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Business rules: listing, reading and scoring quizzes. No servlet code here. */
public class QuizService {

    private static final QuizService SHARED = new QuizService(new QuizRepository());

    private final QuizRepository repository;

    public QuizService(QuizRepository repository) {
        this.repository = repository;
    }

    /** One instance shared by all servlets, so they see the same data. */
    public static QuizService getInstance() {
        return SHARED;
    }

    public List<QuizSummary> listQuizzes() {
        return repository.findAll().stream().map(QuizSummary::from).toList();
    }

    public Quiz getQuiz(int id) {
        Quiz quiz = repository.findById(id);
        if (quiz == null) {
            throw new ApiException(404, "Quiz not found");
        }
        return quiz;
    }

    public Submission submit(int quizId, SubmitRequest request) {
        Quiz quiz = getQuiz(quizId);
        if (request == null || request.answers() == null) {
            throw new ApiException(400, "'answers' is required");
        }

        Set<Integer> answered = new HashSet<>();
        int score = 0;
        for (Answer answer : request.answers()) {
            if (answer == null || answer.questionId() == null || answer.optionId() == null) {
                throw new ApiException(400, "Each answer needs 'questionId' and 'optionId'");
            }
            Question question = quiz.getQuestion(answer.questionId());
            if (question == null) {
                throw new ApiException(400, "Unknown questionId: " + answer.questionId());
            }
            if (!answered.add(answer.questionId())) {
                throw new ApiException(400, "Duplicate answer for question " + answer.questionId());
            }
            if (!question.hasOption(answer.optionId())) {
                throw new ApiException(400, "Unknown optionId " + answer.optionId()
                        + " for question " + answer.questionId());
            }
            if (question.isCorrect(answer.optionId())) {
                score++;
            }
        }

        Submission submission = new Submission(
                UUID.randomUUID().toString(), quizId, score, quiz.getQuestions().size());
        repository.saveSubmission(submission);
        return submission;
    }
}
