package main.java.com.demo.quiz.servlet;

import com.demo.quiz.data.QuizRepository;
import com.demo.quiz.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/api/quizzes/*")
public class SubmitServlet extends HttpServlet {
    private final ObjectMapper json = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        // URL: /api/quizzes/{id}/submit
        String path = req.getPathInfo(); // "/1/submit"
        String[] parts = path.split("/");

        int quizId = Integer.parseInt(parts[1]);
        Quiz quiz = QuizRepository.findById(quizId);
        if (quiz == null) {
            resp.setStatus(404);
            resp.getWriter().write("{\"error\":\"Quiz not found\"}");
            return;
        }

        // Read answers from body
        Map<?, ?> body = json.readValue(req.getInputStream(), Map.class);
        List<Map<String, Object>> answers = (List<Map<String, Object>>) body.get("answers");

        int score = 0;
        for (Map<String, Object> a : answers) {
            int qId = (int) a.get("questionId");
            int optId = (int) a.get("optionId");
            Question q = quiz.getQuestion(qId);
            if (q != null && q.getCorrectOptionId() == optId) score++;
        }

        Submission sub = new Submission(UUID.randomUUID().toString(),
                                        quizId, score, quiz.getQuestions().size());
        QuizRepository.saveSubmission(sub);

        resp.setContentType("application/json");
        json.writeValue(resp.getWriter(), sub);
    }
}