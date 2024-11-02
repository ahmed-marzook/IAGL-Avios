package com.iagl.avios.controller;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.iagl.avios.enums.CabinCode;
import com.iagl.avios.model.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, WebRequest request) {

    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Validation error", ex, request);

    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            error ->
                apiError.addFieldError(
                    error.getObjectName(),
                    error.getField(),
                    error.getRejectedValue(),
                    error.getDefaultMessage()));

    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  protected ResponseEntity<ApiError> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, request);
    apiError.setDebugMessage(ex.getLocalizedMessage());

    if (ex.getCause() instanceof InvalidFormatException
        && ((InvalidFormatException) ex.getCause()).getTargetType().equals(CabinCode.class)) {
      apiError.setMessage(
          "When provided, cabin code must be one of: M (World Traveller), W (World Traveller Plus), J (Club World), F (First)");
      apiError.addFieldError(
          "aviosRequest", // object name
          "cabinCode", // field name
          ((InvalidFormatException) ex.getCause()).getValue(), // rejected value
          "Cabin code must be one of M, W, J, F" // error message
          );
      return new ResponseEntity<>(apiError, apiError.getStatus());
    } else {
      apiError.setMessage("Invalid request format");
    }

    return new ResponseEntity<>(apiError, apiError.getStatus());
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ApiError> handleDefaultExceptions(Exception ex, WebRequest request) {
    ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "UNCAUGHT ERROR", ex, request);
    return new ResponseEntity<>(apiError, apiError.getStatus());
  }
}
