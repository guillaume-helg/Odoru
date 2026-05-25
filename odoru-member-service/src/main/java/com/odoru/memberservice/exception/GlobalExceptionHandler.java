package com.odoru.memberservice.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler to format error responses for the API
 * endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles RuntimeException and returns a BAD_REQUEST status.
   *
   * @param ex the exception to handle
   * @return a ResponseEntity containing the error message
   */
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleRuntimeException(
      final RuntimeException ex) {
    return new ResponseEntity<>(ex.getMessage(),
        HttpStatus.BAD_REQUEST);
  }

  /**
   * Handles validation exceptions and returns map of field errors with
   * BAD_REQUEST status.
   *
   * @param ex the validation exception to handle
   * @return a ResponseEntity containing a map of invalid fields and
   *         their error messages
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      final MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage()));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }
}
