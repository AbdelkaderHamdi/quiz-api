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