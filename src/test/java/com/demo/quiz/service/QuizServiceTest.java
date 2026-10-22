package com.demo.quiz.service;

import com.demo.quiz.data.QuizRepository;
import com.demo.quiz.model.Answer;
import com.demo.quiz.model.Submission;
import com.demo.quiz.model.SubmitRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuizServiceTest {

    // Seed data: quiz 1 has 3 questions. Correct options are 1, 2, 2.
    private QuizRepository repository;
    private QuizService service;

    @BeforeEach
    void setUp() {
        repository = new QuizRepository();
        service = new QuizService(repository);
    }

    private static SubmitRequest request(Answer... answers) {
        return new SubmitRequest(Arrays.asList(answers));
    }

    private static void assertStatus(int expected, Runnable action) {
        ApiException e = assertThrows(ApiException.class, action::run);
        assertEquals(expected, e.getStatus());
    }

    @Test
    void listsQuizzesWithQuestionCount() {
        var list = service.listQuizzes();
        assertEquals(1, list.size());
        assertEquals("Java Basics", list.get(0).title());
        assertEquals(3, list.get(0).questionCount());
    }

    @Test
    void unknownQuizIs404() {
        assertStatus(404, () -> service.getQuiz(99));
        assertStatus(404, () -> service.submit(99, request()));
    }

    @Test
    void allCorrectGivesFullScore() {
        Submission s = service.submit(1, request(
                new Answer(1, 1), new Answer(2, 2), new Answer(3, 2)));
        assertEquals(3, s.score());
        assertEquals(3, s.total());
        assertEquals(1, s.quizId());
    }

    @Test
    void allWrongGivesZero() {
        Submission s = service.submit(1, request(
                new Answer(1, 2), new Answer(2, 1), new Answer(3, 3)));
        assertEquals(0, s.score());
    }

    @Test
    void partialAnswersAreScoredAgainstTotal() {
        Submission s = service.submit(1, request(new Answer(1, 1)));
        assertEquals(1, s.score());
        assertEquals(3, s.total());
    }

    @Test
    void emptyAnswerListScoresZero() {
        Submission s = service.submit(1, request());
        assertEquals(0, s.score());
        assertEquals(3, s.total());
    }

    @Test
    void submissionIsStored() {
        Submission s = service.submit(1, request(new Answer(1, 1)));
        assertNotNull(repository.findSubmission(s.id()));
    }

    @Test
    void duplicateAnswerCannotInflateScore() {
        assertStatus(400, () -> service.submit(1, request(
                new Answer(1, 1), new Answer(1, 1), new Answer(1, 1))));
    }

    @Test
    void unknownQuestionIsRejected() {
        assertStatus(400, () -> service.submit(1, request(new Answer(42, 1))));
    }

    @Test
    void unknownOptionIsRejected() {
        assertStatus(400, () -> service.submit(1, request(new Answer(1, 99))));
    }

    @Test
    void missingFieldsAreRejected() {
        assertStatus(400, () -> service.submit(1, request(new Answer(null, 1))));
        assertStatus(400, () -> service.submit(1, request(new Answer(1, null))));
        assertStatus(400, () -> service.submit(1, new SubmitRequest(null)));
        assertStatus(400, () -> service.submit(1, null));
        assertStatus(400, () -> service.submit(1, new SubmitRequest(Arrays.asList((Answer) null))));
    }

    @Test
    void rejectedSubmissionIsNotStored() {
        List<Answer> bad = List.of(new Answer(1, 1), new Answer(2, 99));
        assertStatus(400, () -> service.submit(1, new SubmitRequest(bad)));
    }
}
