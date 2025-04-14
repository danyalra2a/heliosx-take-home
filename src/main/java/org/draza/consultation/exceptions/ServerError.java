package org.draza.consultation.exceptions;

public class ServerError extends RuntimeException {
  public ServerError(String message) {
    super(message);
  }
}
