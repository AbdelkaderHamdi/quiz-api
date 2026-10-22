package com.demo.quiz.model;

import java.util.List;

/** A quiz: a title and a list of questions. */
public class Quiz {

    private final int id;
    private final String title;
    private final List<Question> questions;

    public Quiz(int id, String title, List<Question> questions) {
        this.id = id;
        this.title = title;
        this.questions = List.copyOf(questions);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    /** Returns the question with this id, or null. */
    public Question getQuestion(int questionId) {
        return questions.stream().filter(q -> q.getId() == questionId).findFirst().orElse(null);
    }
}
