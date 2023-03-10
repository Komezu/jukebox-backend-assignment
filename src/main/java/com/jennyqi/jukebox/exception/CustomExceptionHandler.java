package com.jennyqi.jukebox.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.jennyqi.jukebox.response.ErrorResponse;

@ControllerAdvice
public class CustomExceptionHandler {

  // Handling of missing required query parameter when accessing endpoint
  @ExceptionHandler(MissingServletRequestParameterException.class)
  protected ResponseEntity<ErrorResponse> handleMissingRequestParamException(MissingServletRequestParameterException ex) {
    ErrorResponse error = new ErrorResponse(400, "Bad Request - Missing " + ex.getParameterName());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  // Handling of query parameter not being of right type when accessing endpoint
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected ResponseEntity<ErrorResponse> handleMethodArgTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    String message = "Bad Request - " + ex.getName() + " should be of type " + ex.getRequiredType();
    ErrorResponse error = new ErrorResponse(400, message);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  // Handling of custom runtime exception with a status code and error message
  @ExceptionHandler(MockedApiCallException.class)
  protected ResponseEntity<ErrorResponse> handleMockedApiCallException(MockedApiCallException ex) {
    ErrorResponse error = new ErrorResponse(ex.getStatus(), ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.valueOf(ex.getStatus()));
  }

}
