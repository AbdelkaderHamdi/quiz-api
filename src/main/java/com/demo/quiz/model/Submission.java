package com.demo.quiz.model;

/** The result of one quiz attempt. */
public record Submission(String id, int quizId, int score, int total) {
}
