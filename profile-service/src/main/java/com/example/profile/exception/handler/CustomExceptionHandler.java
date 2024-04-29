package com.example.profile.exception.handler;

import com.example.profile.dto.response.ErrorDetail;
import com.example.profile.dto.response.ErrorResponse;
import com.example.profile.exception.EntityNotFoundException;
import com.mongodb.MongoWriteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorDetail>> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<ErrorDetail> errors = getErrorsFromException(e);
        return new ResponseEntity<>(errors, BAD_REQUEST);
    }

    @ExceptionHandler(MongoWriteException.class)
    public ResponseEntity<ErrorResponse> handleException(MongoWriteException e) {
        return generateErrorResponse(BAD_REQUEST, e);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException e) {
        return generateErrorResponse(NOT_FOUND, e);
    }

    private ResponseEntity<ErrorResponse> generateErrorResponse(HttpStatus status, Exception e) {
        ErrorResponse error = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(error, status);
    }

    private List<ErrorDetail> getErrorsFromException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        return mapFieldErrorsToErrorDetails(fieldErrors);
    }

    private List<ErrorDetail> mapFieldErrorsToErrorDetails(List<FieldError> fieldErrors) {
        return fieldErrors.stream()
                .map(fieldError -> new ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
    }
}
