package com.jennyqi.jukebox;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.jennyqi.jukebox.model.ErrorResponse;

@ControllerAdvice
public class ApplicationErrorHandler {

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingRequestParamException(MissingServletRequestParameterException ex) {
    ErrorResponse error = new ErrorResponse(400, "Bad Request - Missing " + ex.getParameterName());
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    String message = "Bad Request - " + ex.getName() + " should be of type " + ex.getRequiredType();
    ErrorResponse error = new ErrorResponse(400, message);
    return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MockedApiCallException.class)
  public ResponseEntity<ErrorResponse> handleMockedApiCallException(MockedApiCallException ex) {
    ErrorResponse error = new ErrorResponse(ex.getStatus(), ex.getMessage());
    return new ResponseEntity<>(error, HttpStatus.valueOf(ex.getStatus()));
  }

}
