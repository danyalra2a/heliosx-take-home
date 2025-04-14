package org.draza.consultation;

import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import org.draza.consultation.domain.AnsweredQuestions;
import org.draza.consultation.domain.ConsultationResult;
import org.draza.consultation.exceptions.ServerError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Controller {
  private final Service service;

  private static final Logger LOGGER = LoggerFactory.getLogger(Controller.class);

  public Controller(Service service) {
    this.service = service;
  }

  public void getConsultationQuestions(Context context) {
    String consultationId = context.pathParam("consultationId");

    try {
      context.json(service.getQuestionsForConsultation(Integer.parseInt(consultationId)));

    } catch (NumberFormatException ex) {
      String message = "Invalid consultationId passed: " + consultationId;
      LOGGER.warn(message, ex);
      throw exceptionResponse(400,message, ex);

    } catch (ServerError ex) {
      throw exceptionResponse(500,"Error fetching questions for consultation", ex);
    }
  }

  public void postConsultationAnswers(Context context) {
    String consultationId = context.pathParam("consultationId");

    try {
      AnsweredQuestions answers = context.bodyAsClass(AnsweredQuestions.class);

      Integer submissionId =
          service.postConsultationAnswers(Integer.parseInt(consultationId), answers);
      context.status(200).json(Map.of("submissionId", submissionId));

    } catch (NumberFormatException ex) {
      String message = "Invalid consultationId passed: " + consultationId;
      LOGGER.warn(message, ex);
      throw exceptionResponse(400,message, ex);

    } catch (ServerError ex) {
      throw exceptionResponse(500,"Failed to submit answers for consultation", ex);
    }
  }

  public void getConsultationStatus(Context context) {
    String submissionId = context.pathParam("submissionId");

    try {
      ConsultationResult result = service.getConsultationResult(Integer.parseInt(submissionId));
      context.status(200).json(result);

    } catch (NumberFormatException ex) {
      String message = "Invalid submissionId passed: " + submissionId;
      LOGGER.warn(message, ex);
      throw exceptionResponse(400,message, ex);

    } catch (ServerError ex) {
      throw exceptionResponse(500,"Failed to get status", ex);
    }
  }

  private HttpResponseException exceptionResponse(int status, String message, Throwable cause) {
    HttpResponseException ex = new HttpResponseException(status, message);
    ex.initCause(cause);
    return ex;
  }
}
