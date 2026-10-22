package com.demo.quiz.data;

import com.demo.quiz.model.Option;
import com.demo.quiz.model.Question;
import com.demo.quiz.model.Quiz;
import com.demo.quiz.model.Submission;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/** In-memory storage. Thread-safe. Data is lost when the app restarts. */
public class QuizRepository {

    private final Map<Integer, Quiz> quizzes = new ConcurrentSkipListMap<>();
    private final Map<String, Submission> submissions = new ConcurrentHashMap<>();

    public QuizRepository() {
        seed();
    }

    public Collection<Quiz> findAll() {
        return quizzes.values();
    }

    public Quiz findById(int id) {
        return quizzes.get(id);
    }

    public void saveSubmission(Submission submission) {
        submissions.put(submission.id(), submission);
    }

    public Submission findSubmission(String id) {
        return submissions.get(id);
    }

    private void seed() {
        Question q1 = new Question(1, "What does JVM stand for?", List.of(
                new Option(1, "Java Virtual Machine"),
                new Option(2, "Java Very Minimal"),
                new Option(3, "Joint Version Manager")), 1);
        Question q2 = new Question(2, "Which keyword stops a class from being subclassed?", List.of(
                new Option(1, "static"),
                new Option(2, "final"),
                new Option(3, "abstract")), 2);
        Question q3 = new Question(3, "Which collection does not allow duplicate elements?", List.of(
                new Option(1, "ArrayList"),
                new Option(2, "HashSet"),
                new Option(3, "LinkedList")), 2);
        quizzes.put(1, new Quiz(1, "Java Basics", List.of(q1, q2, q3)));
    }
}
