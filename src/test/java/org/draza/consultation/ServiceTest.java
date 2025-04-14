package org.draza.consultation;

import org.draza.consultation.domain.ConsultationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceTest {
  private Repository repository;
  private Service service;

  private static final int SUBMISSION_ID = 1;

  @BeforeEach
  void setUp() {
    this.repository = mock(Repository.class);
    this.service = new Service(repository);
  }

  @Test
  public void calculateConsultationStatus1() {
    setupEligibility(false, false);

    ConsultationStatus status = service.calculateConsultationStatus(SUBMISSION_ID);

    assertEquals(ConsultationStatus.AWAITING_APPROVAL, status);
  }

  @Test
  public void calculateConsultationStatus2() {
    setupEligibility(false, true, false);

    ConsultationStatus status = service.calculateConsultationStatus(SUBMISSION_ID);

    assertEquals(ConsultationStatus.REJECTED, status);
  }

  @Test
  public void calculateConsultationStatus3() {
    setupEligibility(false, true, true);

    ConsultationStatus status = service.calculateConsultationStatus(SUBMISSION_ID);

    assertEquals(ConsultationStatus.APPROVED, status);
  }

  @Test
  public void calculateConsultationStatus4() {
    // ideally a doctor wouldn't verify deal-breaker cases, but manual intervention may occur
    setupEligibility(true, true, false);

    ConsultationStatus status = service.calculateConsultationStatus(SUBMISSION_ID);

    assertEquals(ConsultationStatus.REJECTED, status);
  }

  @Test
  public void calculateConsultationStatus5() {
    setupEligibility(true, false);
    when(repository.isReviewed(SUBMISSION_ID)).thenReturn(false);

    ConsultationStatus status = service.calculateConsultationStatus(SUBMISSION_ID);

    assertEquals(ConsultationStatus.REJECTED, status);
  }

  private void setupEligibility(final boolean includesDealBreaker, final boolean isReviewed) {
    when(repository.hasDealBreakerAnswer(SUBMISSION_ID)).thenReturn(includesDealBreaker);
    when(repository.isReviewed(SUBMISSION_ID)).thenReturn(isReviewed);
  }

  private void setupEligibility(final boolean includesDealBreaker, final boolean isReviewed, final boolean review) {
    when(repository.hasDealBreakerAnswer(SUBMISSION_ID)).thenReturn(includesDealBreaker);
    when(repository.isReviewed(SUBMISSION_ID)).thenReturn(isReviewed);
    when(repository.getReview(SUBMISSION_ID)).thenReturn(review);
  }
}
