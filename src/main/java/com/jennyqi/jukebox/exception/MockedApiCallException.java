package com.jennyqi.jukebox.exception;

// Custom exception that might arise during calls to mocked APIs
// Used to return modified status and message to user depending on context
// Has to extend RUNTIME exception or can't be thrown directly from service to custom exception handler, only propagated

public class MockedApiCallException extends RuntimeException {

  private final int status;

  public MockedApiCallException(int status, String message) {
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }

  // getMessage() is implemented in a parent class, no need to implement here

}
