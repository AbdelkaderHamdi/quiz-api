package com.demo.quiz.servlet;

import com.demo.quiz.service.QuizService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/** GET /api/quizzes : list quizzes (id, title, question count). */
@WebServlet("/api/quizzes")
public class QuizListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Json.write(resp, 200, QuizService.getInstance().listQuizzes());
        } catch (RuntimeException e) {
            log("Unexpected error", e);
            Json.error(resp, 500, "Internal server error");
        }
    }
}
