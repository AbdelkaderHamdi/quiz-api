# Quiz API

A small REST API to list quizzes, read their questions and submit answers to get a score.
Built with Java 17, Maven, Jakarta Servlet 6 and Jackson. Runs on Apache Tomcat 10.1+.

Data is stored **in memory**. It is reset when the app restarts.

## Requirements

- JDK 17 or newer
- Maven 3.8+
- Apache Tomcat 10.1+ (Tomcat 9 and older will NOT work: they use `javax.*`, not `jakarta.*`)

## Build and run

```bash
mvn clean package
cp target/quiz-api.war $TOMCAT_HOME/webapps/
$TOMCAT_HOME/bin/startup.sh        # Windows: %TOMCAT_HOME%\bin\startup.bat
```

Check that it works:

```bash
curl http://localhost:8080/quiz-api/api/quizzes
```

## Run the tests

```bash
mvn test
```

- `QuizServiceTest`: scoring rules and validation (plain unit tests).
- `ApiIntegrationTest`: starts an embedded Tomcat and calls the real endpoints over HTTP.

## Configuration

There is no configuration and there are no secrets: no database, no API keys, no `.env` file.
The port and the base path (`/quiz-api`) come from Tomcat and the WAR name.

## API

Base URL: `http://localhost:8080/quiz-api`

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/quizzes` | List quizzes (id, title, question count) |
| GET | `/api/quizzes/{id}/questions` | Questions and options. Correct answers are never returned. |
| POST | `/api/quizzes/{id}/submit` | Send answers, get the score |

### List quizzes

```bash
curl http://localhost:8080/quiz-api/api/quizzes
```
```json
[{"id":1,"title":"Java Basics","questionCount":3}]
```

### Get questions

```bash
curl http://localhost:8080/quiz-api/api/quizzes/1/questions
```
```json
{"id":1,"title":"Java Basics","questions":[
  {"id":1,"text":"What does JVM stand for?",
   "options":[{"id":1,"text":"Java Virtual Machine"},{"id":2,"text":"Java Very Minimal"},{"id":3,"text":"Joint Version Manager"}]}
]}
```

### Submit answers

```bash
$body = @{
    answers = @(
        @{ questionId = 1; optionId = 1 },
        @{ questionId = 2; optionId = 2 }
    )
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/quiz-api/api/quizzes/1/submit" -Method Post -ContentType "application/json" -Body $body
```
```json
{"id":"3f0c...","quizId":1,"score":2,"total":3}
```

Rules: each question can be answered once, `questionId` and `optionId` must exist, and
unanswered questions count as wrong. `score` is out of `total` (all questions in the quiz).

### Errors

All errors use the same shape:

```json
{"error":"Quiz not found"}
```

| Status | When |
|--------|------|
| 400 | Invalid JSON, missing field, unknown question or option, duplicate answer, bad quiz id |
| 404 | Unknown quiz or unknown path |
| 405 | Wrong HTTP method (for example GET on `/submit`) |
| 413 | Request body larger than 100 KB (checked when the size is declared) |
| 500 | Unexpected error (details are logged, not sent to the client) |

## Project layout

```
src/main/java/com/demo/quiz/
  model/     Quiz, Question, Option, Submission, QuizSummary, Answer, SubmitRequest
  data/      QuizRepository       in-memory storage with seed data
  service/   QuizService          scoring and validation, ApiException
  servlet/   QuizListServlet      GET /api/quizzes
             QuizServlet          GET .../questions and POST .../submit
             Json                 JSON response helper
src/test/java/com/demo/quiz/      unit and integration tests
```

## Known limits

- In-memory data: no persistence, and submissions are never deleted.
- No way to create or edit quizzes through the API (one seed quiz).
- No authentication and no rate limiting.

# Quiz API

A simple REST API for creating and taking quizzes, built with **Maven**,
**Java Servlets**, and deployed on **Apache Tomcat**.

## Tech Stack
- Java 17
- Maven
- Jakarta Servlet API
- Jackson (JSON)
- Apache Tomcat 10+

## Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| GET    | /api/quizzes | List all quizzes |
| GET    | /api/quizzes/{id}/questions | Get questions |
| POST   | /api/quizzes/{id}/submit | Submit answers, get score |

## Build & Run

```bash
mvn clean package
cp target/quiz-api.war $TOMCAT_HOME/webapps/
$TOMCAT_HOME/bin/startup.sh

Then visit: http://localhost:8080/quiz-api/api/quizzes

# work on a feature
git checkout -b feature/submit-endpoint

# ... code, code, code ...

git add .
git commit -m "Add submit endpoint with scoring logic"
git push -u origin feature/submit-endpoint