package org.draza.consultation;

import org.draza.consultation.domain.ConsultationStatus;
import org.draza.consultation.domain.Question;
import org.draza.consultation.domain.Answer;
import org.draza.consultation.domain.AnsweredQuestion;
import org.draza.consultation.domain.AnsweredQuestions;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Repository {
  private final Jdbi jdbi;

  public Repository() {
    String dbUrl = System.getenv("DB_URL");
    String dbUsername = System.getenv("DB_USER");
    String dbPassword = System.getenv("DB_PASSWORD");

    if (dbUrl == null || dbUsername == null || dbPassword == null) {
      throw new IllegalStateException("Missing DB env variables");
    }

    jdbi = Jdbi.create(dbUrl, dbUsername, dbPassword);
  }

  public List<Question> getQuestionsForConsultation(int id) {
    List<Question> res = new ArrayList<>();

    String sql = """
    SELECT
    cq.question_id,
    q.question_text,
    a_o.answer_option_id,
    a_o.answer_option
    FROM consultations c
    JOIN consultation_questions cq
      ON cq.consultation_id = c.consultation_id
    JOIN questions q
      ON cq.question_id = q.question_id
    JOIN answer_options a_o
      ON a_o.question_id = q.question_id
    WHERE c.consultation_id = :consultationId
    ORDER BY cq.placement ASC
    """;

    jdbi.useHandle(
        handle -> {
          Map<Integer, Question> questionMap = new LinkedHashMap<>();

          handle
              .createQuery(sql)
              .bind("consultationId", id)
              .map(
                  (rs, ctx) -> {
                    int questionId = rs.getInt("question_id");
                    String questionText = rs.getString("question_text");
                    int answerOptionId = rs.getInt("answer_option_id");
                    String answerOption = rs.getString("answer_option");

                    questionMap
                        .computeIfAbsent(
                            questionId, qid -> new Question(qid, questionText, new ArrayList<>()))
                        .answers()
                        .add(new Answer(answerOptionId, answerOption));

                    return null;
                  })
              .list();

          res.addAll(questionMap.values());
        });
    return res;
  }

  public Integer postConsultationAnswers(int consultationId, AnsweredQuestions answers) {
    Integer submissionId = createSubmission(consultationId);
    insertAnswers(submissionId, answers);
    return submissionId;
  }

  public void updateSubmissionStatus(int submissionId, ConsultationStatus status) {
    String sql = """
    UPDATE submissions
    SET status_id = :statusId
    WHERE submission_id = :submissionId
    """;

    jdbi.useTransaction(handle -> {
      handle.createUpdate(sql)
          .bind("statusId", status.getId())
          .bind("submissionId", submissionId)
          .execute();
    });
  }

  public ConsultationStatus getSubmissionStatus(int submissionId) {
    String sql = """
    SELECT status_id FROM submissions
    WHERE submission_id = :submissionId
    """;

    int statusId = jdbi.withHandle(handle ->
        handle.createQuery(sql)
            .bind("submissionId", submissionId)
            .mapTo(int.class)
            .findOne()
            .orElseThrow(() -> new IllegalArgumentException("Status not found for submission: " + submissionId))
    );

    return ConsultationStatus.fromId(statusId);
  }

  public boolean isReviewed(int submissionId) {
    String sql = "SELECT reviewed FROM submissions WHERE submission_id = :submissionId";

    return jdbi.withHandle(handle ->
        handle.createQuery(sql)
            .bind("submissionId", submissionId)
            .mapTo(boolean.class)
            .one()
    );
  }

  public boolean getReview(int submissionId) {
    String sql = "SELECT reviewe FROM submissions WHERE submission_id = :submissionId";

    return jdbi.withHandle(handle ->
        handle.createQuery(sql)
            .bind("submissionId", submissionId)
            .mapTo(boolean.class)
            .one()
    );
  }

  public boolean hasDealBreakerAnswer(int submissionId) {
    String sql = """
    SELECT SUM(ao.deal_breaker) > 0
    FROM submitted_answers sa
    JOIN answer_options ao ON sa.answer_option_id = ao.answer_option_id
    WHERE sa.submission_id = :submissionId
    """;

    return jdbi.withHandle(handle ->
        handle.createQuery(sql)
            .bind("submissionId", submissionId)
            .mapTo(boolean.class)
            .one()
    );
  }

  private Integer createSubmission(int consultationId) {
    String submissionCreationSql = """
    INSERT INTO submissions (consultation_id)
    VALUES (:consultationId)
    """;

    return jdbi.withHandle(handle -> handle
        .createUpdate(submissionCreationSql)
        .bind("consultationId", consultationId)
        .executeAndReturnGeneratedKeys("submission_id")
        .mapTo(int.class)
        .one());
  }

  private void insertAnswers(int submissionId, AnsweredQuestions answers) {
    String insertAnswersSql = """
    INSERT INTO submitted_answers (submission_id, question_id, answer_option_id)
    VALUES (:submissionId, :questionId, :answer_option_id)
    """;

    jdbi.useTransaction(handle -> {
      PreparedBatch batch = handle.prepareBatch(insertAnswersSql);
      for (AnsweredQuestion answer : answers.answeredQuestions()) {
        batch
            .bind("submissionId", submissionId)
            .bind("questionId", answer.questionId())
            .bind("answer_option_id", answer.answerId())
            .add();
      }
      batch.execute();
    });
  }
}
