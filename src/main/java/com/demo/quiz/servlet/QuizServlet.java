package com.demo.quiz.servlet;

import com.demo.quiz.model.SubmitRequest;
import com.demo.quiz.service.ApiException;
import com.demo.quiz.service.QuizService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handles everything under /api/quizzes/{id}/...
 *   GET  /api/quizzes/{id}/questions  : questions and options (no correct answers)
 *   POST /api/quizzes/{id}/submit     : send answers, get the score
 *
 * Replaces the old QuestionServlet and SubmitServlet: two servlets cannot
 * share the same URL pattern, so one servlet routes both actions.
 */
@WebServlet("/api/quizzes/*")
public class QuizServlet extends HttpServlet {

    private static final int MAX_BODY_BYTES = 100_000;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int quizId = route(req, resp, "questions", "GET");
            Json.write(resp, 200, QuizService.getInstance().getQuiz(quizId));
        } catch (ApiException e) {
            Json.error(resp, e.getStatus(), e.getMessage());
        } catch (RuntimeException e) {
            log("Unexpected error", e);
            Json.error(resp, 500, "Internal server error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            int quizId = route(req, resp, "submit", "POST");
            if (req.getContentLengthLong() > MAX_BODY_BYTES) {
                throw new ApiException(413, "Request body too large");
            }
            SubmitRequest body;
            try {
                body = Json.MAPPER.readValue(req.getInputStream(), SubmitRequest.class);
            } catch (JsonProcessingException e) {
                throw new ApiException(400, "Invalid JSON body");
            }
            Json.write(resp, 201, QuizService.getInstance().submit(quizId, body));
        } catch (ApiException e) {
            Json.error(resp, e.getStatus(), e.getMessage());
        } catch (RuntimeException e) {
            log("Unexpected error", e);
            Json.error(resp, 500, "Internal server error");
        }
    }

    /**
     * Checks that the path is /{id}/{expectedAction} and returns the quiz id.
     * Throws 404 for unknown paths, 400 for a bad id, 405 for a wrong HTTP method.
     */
    private int route(HttpServletRequest req, HttpServletResponse resp,
                      String expectedAction, String allowedMethod) {
        String path = req.getPathInfo();
        if (path == null || path.length() < 2) {
            throw new ApiException(404, "Not found");
        }
        String[] parts = path.substring(1).split("/");
        if (parts.length != 2) {
            throw new ApiException(404, "Not found");
        }
        String action = parts[1];
        boolean knownAction = action.equals("questions") || action.equals("submit");
        if (!knownAction) {
            throw new ApiException(404, "Not found");
        }
        if (!action.equals(expectedAction)) {
            resp.setHeader("Allow", action.equals("questions") ? "GET" : "POST");
            throw new ApiException(405, "Method not allowed");
        }
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid quiz id");
        }
    }
}
