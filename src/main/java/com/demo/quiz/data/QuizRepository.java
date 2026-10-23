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

    Question q4 = new Question(4, "Which keyword is used to inherit a class?", List.of(
            new Option(1, "implements"),
            new Option(2, "extends"),
            new Option(3, "inherits")), 2);

    Question q5 = new Question(5, "Which method is the entry point of a Java application?", List.of(
            new Option(1, "start()"),
            new Option(2, "run()"),
            new Option(3, "main()")), 3);

    Question q6 = new Question(6, "Which data type stores true or false values?", List.of(
            new Option(1, "boolean"),
            new Option(2, "int"),
            new Option(3, "String")), 1);

    Question q7 = new Question(7, "Which operator checks equality between primitive values?", List.of(
            new Option(1, "="),
            new Option(2, "=="),
            new Option(3, "!=")), 2);

    Question q8 = new Question(8, "Which keyword creates an object in Java?", List.of(
            new Option(1, "class"),
            new Option(2, "this"),
            new Option(3, "new")), 3);

    Question q9 = new Question(9, "Which access modifier allows access only within the same class?", List.of(
            new Option(1, "public"),
            new Option(2, "private"),
            new Option(3, "protected")), 2);

    Question q10 = new Question(10, "Which keyword refers to the current object?", List.of(
            new Option(1, "this"),
            new Option(2, "super"),
            new Option(3, "self")), 1);

    Question q11 = new Question(11, "Which class is the parent of all Java classes?", List.of(
            new Option(1, "Main"),
            new Option(2, "Object"),
            new Option(3, "Class")), 2);

    Question q12 = new Question(12, "Which loop executes its body at least once?", List.of(
            new Option(1, "for"),
            new Option(2, "while"),
            new Option(3, "do-while")), 3);

    Question q13 = new Question(13, "Which keyword is used to implement an interface?", List.of(
            new Option(1, "extends"),
            new Option(2, "implements"),
            new Option(3, "interface")), 2);

    Question q14 = new Question(14, "Which exception is thrown when dividing an integer by zero?", List.of(
            new Option(1, "ArithmeticException"),
            new Option(2, "IOException"),
            new Option(3, "NullPointerException")), 1);

    Question q15 = new Question(15, "Which keyword prevents a method from being overridden?", List.of(
            new Option(1, "static"),
            new Option(2, "final"),
            new Option(3, "private")), 2);

    Question q16 = new Question(16, "Which collection maintains elements in insertion order and allows duplicates?", List.of(
            new Option(1, "HashSet"),
            new Option(2, "ArrayList"),
            new Option(3, "TreeSet")), 2);

    Question q17 = new Question(17, "What is method overloading?", List.of(
            new Option(1, "Using the same method name with different parameter lists"),
            new Option(2, "Replacing a parent method in a child class"),
            new Option(3, "Creating multiple classes with the same name")), 1);

    Question q18 = new Question(18, "Which keyword is used to handle an exception?", List.of(
            new Option(1, "throws"),
            new Option(2, "try"),
            new Option(3, "throw")), 2);

    Question q19 = new Question(19, "What is the default value of an instance variable of type int?", List.of(
            new Option(1, "null"),
            new Option(2, "1"),
            new Option(3, "0")), 3);

    Question q20 = new Question(20, "Which keyword allows a class to implement multiple interfaces?", List.of(
            new Option(1, "implements"),
            new Option(2, "extends"),
            new Option(3, "instanceof")), 1);

    quizzes.put(1, new Quiz(1, "Java Basics", List.of(
            q1, q2, q3, q4, q5,
            q6, q7, q8, q9, q10,
            q11, q12, q13, q14, q15,
            q16, q17, q18, q19, q20
    )));
    }
}
