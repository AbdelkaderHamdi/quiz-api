package com.demo.quiz.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Starts a real embedded Tomcat, loads the compiled classes (so the
 * @WebServlet annotations are used) and calls the API over HTTP.
 */
class ApiIntegrationTest {

    private static Tomcat tomcat;
    private static String base;
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void startServer() throws Exception {
        Path work = Files.createTempDirectory("quiz-api-test");
        Path docBase = Files.createDirectories(work.resolve("webapp"));

        tomcat = new Tomcat();
        tomcat.setBaseDir(work.toString());
        tomcat.setPort(0);
        tomcat.getConnector();

        Context ctx = tomcat.addWebapp("", docBase.toString());
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(
                resources, "/WEB-INF/classes",
                Path.of("target", "classes").toAbsolutePath().toString(), "/"));
        ctx.setResources(resources);

        tomcat.start();
        base = "http://127.0.0.1:" + tomcat.getConnector().getLocalPort();
    }

    @AfterAll
    static void stopServer() throws Exception {
        tomcat.stop();
        tomcat.destroy();
    }

    private static HttpResponse<String> get(String path) throws Exception {
        return CLIENT.send(HttpRequest.newBuilder(URI.create(base + path)).GET().build(),
                HttpResponse.BodyHandlers.ofString());
    }

    private static HttpResponse<String> post(String path, String body) throws Exception {
        return CLIENT.send(HttpRequest.newBuilder(URI.create(base + path))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body)).build(),
                HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void listQuizzes() throws Exception {
        HttpResponse<String> r = get("/api/quizzes");
        assertEquals(200, r.statusCode());
        assertTrue(r.headers().firstValue("Content-Type").orElse("").startsWith("application/json"));
        JsonNode json = MAPPER.readTree(r.body());
        assertEquals("Java Basics", json.get(0).get("title").asText());
        assertEquals(3, json.get(0).get("questionCount").asInt());
    }

    @Test
    void questionsDoNotExposeCorrectAnswers() throws Exception {
        HttpResponse<String> r = get("/api/quizzes/1/questions");
        assertEquals(200, r.statusCode());
        JsonNode json = MAPPER.readTree(r.body());
        assertEquals(3, json.get("questions").size());
        assertEquals(3, json.get("questions").get(0).get("options").size());
        assertFalse(r.body().toLowerCase().contains("correct"));
    }

    @Test
    void listDoesNotExposeQuestions() throws Exception {
        String body = get("/api/quizzes").body();
        assertFalse(body.contains("questions\""));
        assertFalse(body.toLowerCase().contains("correct"));
    }

    @Test
    void unknownQuizIs404() throws Exception {
        HttpResponse<String> r = get("/api/quizzes/99/questions");
        assertEquals(404, r.statusCode());
        assertEquals("Quiz not found", MAPPER.readTree(r.body()).get("error").asText());
    }

    @Test
    void badQuizIdIs400() throws Exception {
        assertEquals(400, get("/api/quizzes/abc/questions").statusCode());
    }

    @Test
    void unknownPathIs404() throws Exception {
        assertEquals(404, get("/api/quizzes/1/nothing").statusCode());
        assertEquals(404, get("/api/quizzes/1").statusCode());
    }

    @Test
    void wrongMethodIs405() throws Exception {
        assertEquals(405, get("/api/quizzes/1/submit").statusCode());
        assertEquals(405, post("/api/quizzes/1/questions", "{}").statusCode());
    }

    @Test
    void submitAllCorrect() throws Exception {
        HttpResponse<String> r = post("/api/quizzes/1/submit", """
                {"answers":[{"questionId":1,"optionId":1},
                            {"questionId":2,"optionId":2},
                            {"questionId":3,"optionId":2}]}""");
        assertEquals(201, r.statusCode());
        JsonNode json = MAPPER.readTree(r.body());
        assertEquals(3, json.get("score").asInt());
        assertEquals(3, json.get("total").asInt());
        assertFalse(json.get("id").asText().isBlank());
    }

    @Test
    void submitPartlyWrong() throws Exception {
        HttpResponse<String> r = post("/api/quizzes/1/submit", """
                {"answers":[{"questionId":1,"optionId":1},{"questionId":2,"optionId":1}]}""");
        assertEquals(201, r.statusCode());
        assertEquals(1, MAPPER.readTree(r.body()).get("score").asInt());
    }

    @Test
    void submitToUnknownQuizIs404() throws Exception {
        assertEquals(404, post("/api/quizzes/99/submit", "{\"answers\":[]}").statusCode());
    }

    @Test
    void invalidBodiesAre400() throws Exception {
        assertEquals(400, post("/api/quizzes/1/submit", "not json").statusCode());
        assertEquals(400, post("/api/quizzes/1/submit", "").statusCode());
        assertEquals(400, post("/api/quizzes/1/submit", "{}").statusCode());
        assertEquals(400, post("/api/quizzes/1/submit", "{\"answers\":\"x\"}").statusCode());
        assertEquals(400, post("/api/quizzes/1/submit",
                "{\"answers\":[{\"questionId\":\"one\",\"optionId\":1}]}").statusCode());
        assertEquals(400, post("/api/quizzes/1/submit",
                "{\"answers\":[{\"questionId\":1}]}").statusCode());
    }

    @Test
    void duplicateAnswersAre400() throws Exception {
        HttpResponse<String> r = post("/api/quizzes/1/submit", """
                {"answers":[{"questionId":1,"optionId":1},{"questionId":1,"optionId":1}]}""");
        assertEquals(400, r.statusCode());
    }
}
