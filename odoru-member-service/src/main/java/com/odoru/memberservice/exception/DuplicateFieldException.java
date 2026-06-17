package com.odoru.memberservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DuplicateFieldException extends ResponseStatusException {

  public DuplicateFieldException(final String field) {
    super(HttpStatus.CONFLICT, field + " already in use");
  }
}
