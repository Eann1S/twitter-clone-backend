package com.example.profile.exception.handler;

import com.example.profile.dto.response.ErrorResponse;
import com.example.profile.exception.ActionNotAllowedException;
import com.example.profile.exception.EntityNotFoundException;
import com.mongodb.MongoWriteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors, BAD_REQUEST);
    }

    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<ErrorResponse> handleException(MongoWriteException e) {
        return generateDefaultErrorResponse(BAD_REQUEST, e);
    }

    @ExceptionHandler(ActionNotAllowedException.class)
    public ResponseEntity<ErrorResponse> handleException(ActionNotAllowedException e) {
        return generateDefaultErrorResponse(FORBIDDEN, e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException e) {
        return generateDefaultErrorResponse(NOT_FOUND, e);
    }

    private ResponseEntity<ErrorResponse> generateDefaultErrorResponse(HttpStatus status, Exception e) {
        ErrorResponse error = new ErrorResponse(e.getMessage());

        return new ResponseEntity<>(error, status);
    }
}
