package com.demo.quiz.model;

/** Short view of a quiz for the list endpoint (no questions, no answers). */
public record QuizSummary(int id, String title, int questionCount) {

    public static QuizSummary from(Quiz quiz) {
        return new QuizSummary(quiz.getId(), quiz.getTitle(), quiz.getQuestions().size());
    }
}
