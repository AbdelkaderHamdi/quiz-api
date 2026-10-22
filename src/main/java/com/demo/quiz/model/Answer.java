package com.demo.quiz.model;

/** One answer in a submit request. Integer (not int) so a missing field is null, not 0. */
public record Answer(Integer questionId, Integer optionId) {
}
