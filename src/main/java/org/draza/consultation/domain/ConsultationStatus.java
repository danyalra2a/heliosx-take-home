package org.draza.consultation.domain;

public enum ConsultationStatus {
  UNPROCESSED(1, "Unprocessed"),
  REJECTED(2, "Rejected"),
  AWAITING_APPROVAL(3, "Awaiting Approval"),
  APPROVED(4, "Approved");

  private final int id;
  private final String readableName;

  ConsultationStatus(int id, String readableName) {
    this.id = id;
    this.readableName = readableName;
  }

  public int getId() {
    return id;
  }

  public String getReadableName() {
    return readableName;
  }

  public static ConsultationStatus fromId(int id) {
    for (ConsultationStatus status : values()) {
      if (status.id == id) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown id: " + id);
  }
}
