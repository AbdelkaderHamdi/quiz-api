package com.demo.quiz.model;

import java.util.List;

/**
 * A question with its options.
 *
 * The correct option id is private and has NO getter, so Jackson never
 * writes it to JSON. Use {@link #isCorrect(int)} to check an answer.
 */
public class Question {

    private final int id;
    private final String text;
    private final List<Option> options;
    private final int correctOptionId;

    public Question(int id, String text, List<Option> options, int correctOptionId) {
        if (options.stream().noneMatch(o -> o.id() == correctOptionId)) {
            throw new IllegalArgumentException("Correct option must be one of the options");
        }
        this.id = id;
        this.text = text;
        this.options = List.copyOf(options);
        this.correctOptionId = correctOptionId;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Option> getOptions() {
        return options;
    }

    public boolean hasOption(int optionId) {
        return options.stream().anyMatch(o -> o.id() == optionId);
    }

    public boolean isCorrect(int optionId) {
        return optionId == correctOptionId;
    }
}
