package com.jennyqi.jukebox;

public class MockedApiCallException extends Exception {

  private final int status;
  private final String message;

  public MockedApiCallException(int status, String message) {
    super(message);
    this.status = status;
    this.message = message;
  }

  public int getStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }

}
