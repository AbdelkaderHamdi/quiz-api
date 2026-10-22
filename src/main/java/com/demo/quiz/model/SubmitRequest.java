package com.demo.quiz.model;

import java.util.List;

/** Body of POST /api/quizzes/{id}/submit. */
public record SubmitRequest(List<Answer> answers) {
}
