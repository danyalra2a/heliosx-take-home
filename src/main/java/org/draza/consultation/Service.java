package org.draza.consultation;

import org.draza.consultation.domain.*;
import org.draza.consultation.exceptions.ServerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Service {
  private final Repository repository;

  private static final Logger LOGGER = LoggerFactory.getLogger(Service.class);

  public Service(Repository repository) {
    this.repository = repository;
  }

  public List<Question> getQuestionsForConsultation(int consultationId) {
    try {
      return repository.getQuestionsForConsultation(consultationId);
    } catch (Exception ex) {
      LOGGER.error(
          "Server error when accessing questions for consultation with id: {}", consultationId, ex);
      throw new ServerError("Unable to fetch questions for consultation " + consultationId);
    }
  }

  public Integer postConsultationAnswers(int consultationId, AnsweredQuestions answers) {
    try {
      int submissionId = repository.postConsultationAnswers(consultationId, answers);

      // process submission
      ConsultationStatus status = calculateConsultationStatus(submissionId);
      repository.updateSubmissionStatus(submissionId, status);

      return submissionId;
    } catch (Exception ex) {
      LOGGER.error(
          "Server error when posting submission for consultation with id: {}", consultationId, ex);
      throw new ServerError("Unable to post submission for consultation " + consultationId);
    }
  }

  public ConsultationResult getConsultationResult(int submissionId) {
    try {
      ConsultationStatus status = repository.getSubmissionStatus(submissionId);

      return switch (status) {
        case ConsultationStatus.UNPROCESSED -> new ConsultationResult(status, "kaboom!");
        case ConsultationStatus.REJECTED -> new ConsultationResult(status, "You're not eligible.");
        case ConsultationStatus.AWAITING_APPROVAL ->
            new ConsultationResult(status, "Awaiting approval from a doctor.");
        case ConsultationStatus.APPROVED ->
            new ConsultationResult(status, "Your account will be charged soon.");
      };
    } catch (Exception ex) {
      LOGGER.error(
          "Server error when getting submission status for submission with id: {}", submissionId, ex);
      throw new ServerError("Unable to get submission status for submission: " + submissionId);
    }
  }

  ConsultationStatus calculateConsultationStatus(int submissionId) {
    boolean reviewed = repository.isReviewed(submissionId);
    boolean includesDealBreaker = repository.hasDealBreakerAnswer(submissionId);

    if (includesDealBreaker) {
      return ConsultationStatus.REJECTED;
    } else if (!reviewed) {
      return ConsultationStatus.AWAITING_APPROVAL;
    } else {
      boolean review = repository.getReview(submissionId);
      if (review) {
        return ConsultationStatus.APPROVED;
      } else {
        return ConsultationStatus.REJECTED;
      }
    }
  }
}
