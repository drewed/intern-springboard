/*
 * © 2026 by Intellectual Reserve, Inc. All rights reserved.
 */

package org.familysearch.intern.springboard.ws.controller;

import java.io.IOException;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@ControllerAdvice
public class ValidationExceptionHandler {

  @ExceptionHandler(HandlerMethodValidationException.class)
  public void handleValidationException(HandlerMethodValidationException ex, HttpServletResponse response) throws IOException {
    String errorMessage = ex.getAllErrors().stream()
        .findFirst()
        .map(error -> error.getDefaultMessage())
        .orElse("Validation failure");

    response.sendError(HttpStatus.BAD_REQUEST.value(), errorMessage);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public void handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HandlerMethod handlerMethod, HttpServletResponse response) throws IOException {
    String errorMessage = "Validation failure";

    if (handlerMethod != null) {
      // Find the @RequestBody parameter with @NotBlank annotation
      MethodParameter[] parameters = handlerMethod.getMethodParameters();
      for (MethodParameter parameter : parameters) {
        if (parameter.hasParameterAnnotation(RequestBody.class)) {
          NotBlank notBlank = parameter.getParameterAnnotation(NotBlank.class);
          if (notBlank != null && !notBlank.message().isEmpty()) {
            errorMessage = notBlank.message();
            break;
          }
        }
      }
    }

    response.sendError(HttpStatus.BAD_REQUEST.value(), errorMessage);
  }
}
