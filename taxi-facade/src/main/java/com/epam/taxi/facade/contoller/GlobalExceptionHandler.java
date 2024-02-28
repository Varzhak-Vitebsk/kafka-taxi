package com.epam.taxi.facade.contoller;

import com.epam.taxi.facade.exception.InvalidRequestException;
import com.epam.taxi.facade.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final String ERROR_MESSAGE = "Exception has occurred: ";

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex,
                                                        HttpServletRequest request) {
    log.error(ERROR_MESSAGE, ex);
    return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
  }

  private ResponseEntity<ErrorResponse> buildResponse(final String message, HttpStatus httpStatus,
      HttpServletRequest request) {
    final var body = ErrorResponse.builder()
        .timestamp(Instant.now())
        .status(httpStatus.value())
        .error(message)
        .path(request.getRequestURI())
        .build();

    return ResponseEntity.status(httpStatus)
        .contentType(MediaType.APPLICATION_JSON)
        .body(body);
  }

}
