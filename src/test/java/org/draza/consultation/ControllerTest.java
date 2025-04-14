package org.draza.consultation;

import io.javalin.http.Context;
import io.javalin.http.HttpResponseException;
import org.draza.consultation.domain.Question;
import org.draza.consultation.exceptions.ServerError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ControllerTest {
  private Service service;
  private Controller controller;
  private Context context;

  private static final List<Question> TEST_QUESTIONS = List.of(new Question(1, "test q", List.of()));

  @BeforeEach
  void setUp() {
    this.service = mock(Service.class);
    this.controller = new Controller(service);
    this.context = mock(Context.class);
  }

  @Test
  void getConsultationQuestionsSuccess() {
    when(context.pathParam("consultationId")).thenReturn("1");
    when(service.getQuestionsForConsultation(1)).thenReturn(TEST_QUESTIONS);

    controller.getConsultationQuestions(context);

    verify(context).json(TEST_QUESTIONS);
  }

  @Test
  void getConsultationQuestionsInvalidInput() {
    when(context.pathParam("consultationId")).thenReturn("in");

    HttpResponseException ex =
        assertThrows(HttpResponseException.class, () -> controller.getConsultationQuestions(context));

    assertEquals(400, ex.getStatus());
  }

  @Test
  void getConsultationQuestionsServiceFailure() {
    when(context.pathParam("consultationId")).thenReturn("1");
    when(service.getQuestionsForConsultation(1)).thenThrow(new ServerError("PSU died"));

    HttpResponseException ex =
        assertThrows(HttpResponseException.class, () -> controller.getConsultationQuestions(context));

    assertEquals(500, ex.getStatus());
  }
}
