package main.java.com.demo.quiz.data;

import com.demo.quiz.model.*;
import java.util.*;

public class QuizRepository {
    private static final Map<Integer, Quiz> QUIZZES = new HashMap<>();
    private static final Map<String, Submission> SUBMISSIONS = new HashMap<>();

    static {
        // Seed a sample quiz
        Quiz q = new Quiz(1, "Java Basics");
        Question q1 = new Question(1, "What is JVM?");
        q1.addOption(new Option(1, "Java Virtual Machine"));
        q1.addOption(new Option(2, "Java Very Minimal"));
        q1.setCorrectOptionId(1);
        q.addQuestion(q1);
        // ... add more questions
        QUIZZES.put(1, q);
    }

    public static Collection<Quiz> findAll() { return QUIZZES.values(); }
    public static Quiz findById(int id) { return QUIZZES.get(id); }
    public static void saveSubmission(Submission s) { SUBMISSIONS.put(s.getId(), s); }
}