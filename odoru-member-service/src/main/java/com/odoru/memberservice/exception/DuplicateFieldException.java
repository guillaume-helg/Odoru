package com.odoru.memberservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Thrown when a unique field constraint is violated (e.g. duplicate
 * email or username during registration).
 */
public class DuplicateFieldException extends ResponseStatusException {

  public DuplicateFieldException(final String field) {
    super(HttpStatus.CONFLICT, field + " already in use");
  }
}
