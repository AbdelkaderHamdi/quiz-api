package main.java.com.demo.quiz.servlet;

import com.demo.quiz.data.QuizRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/api/quizzes")
public class QuizListServlet extends HttpServlet {
    private final ObjectMapper json = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        json.writeValue(resp.getWriter(), QuizRepository.findAll());
    }
}