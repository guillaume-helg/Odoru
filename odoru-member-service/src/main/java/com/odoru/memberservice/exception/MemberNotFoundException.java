package com.odoru.memberservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Thrown when a member lookup fails because no record exists for the
 * requested identifier.
 */
public class MemberNotFoundException extends ResponseStatusException {

  public MemberNotFoundException(final String id) {
    super(HttpStatus.NOT_FOUND, "Member not found with id: " + id);
  }
}
