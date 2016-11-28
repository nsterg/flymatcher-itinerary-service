package com.flymatcher.itinerary.exception;

import com.flymatcher.error.FlymatcherError;

public class SkyscannerAdaptorBadRequestException extends RuntimeException {

  private static final long serialVersionUID = -2193279443658669664L;

  private final FlymatcherError error;

  public SkyscannerAdaptorBadRequestException(final String message, final FlymatcherError error) {
    super(message);
    this.error = error;
  }

  public FlymatcherError getError() {
    return error;
  }


}
