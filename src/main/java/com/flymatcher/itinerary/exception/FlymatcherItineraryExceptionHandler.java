package com.flymatcher.itinerary.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.flymatcher.error.ErrorType;
import com.flymatcher.error.FlymatcherError;

@ControllerAdvice
public class FlymatcherItineraryExceptionHandler {

  @ExceptionHandler(value = {SkyscannerAdaptorServerException.class})
  public ResponseEntity<FlymatcherError> handleException(
      final SkyscannerAdaptorServerException exception) {

    return buildErrorResponse(INTERNAL_SERVER_ERROR, ErrorType.INTERNAL_SERVER_ERROR,
        exception.getMessage());
  }

  @ExceptionHandler(value = {SkyscannerAdaptorBadRequestException.class})
  public ResponseEntity<FlymatcherError> handleException(
      final SkyscannerAdaptorBadRequestException exception) {

    return new ResponseEntity<>(exception.getError(), BAD_REQUEST);
  }

  private ResponseEntity<FlymatcherError> buildErrorResponse(final HttpStatus status,
      final ErrorType errorType, final String message) {

    return new ResponseEntity<>(buildFlymatcherError(message, errorType), status);
  }

  private FlymatcherError buildFlymatcherError(final String message, final ErrorType errorType) {
    final FlymatcherError flymatcherError = new FlymatcherError();

    flymatcherError.setErrorDescription(message);
    flymatcherError.setErrorType(errorType);

    return flymatcherError;
  }

}
