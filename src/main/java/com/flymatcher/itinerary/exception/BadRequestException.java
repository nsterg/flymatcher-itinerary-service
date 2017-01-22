package com.flymatcher.itinerary.exception;

public class BadRequestException extends RuntimeException {

  private static final long serialVersionUID = 812391745338825656L;

  public BadRequestException(final String message) {
    super(message);
  }

}
