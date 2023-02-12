package com.jennyqi.jukebox.exception;

public class MockedApiCallException extends RuntimeException {

  private final int status;

  public MockedApiCallException(int status, String message) {
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

}