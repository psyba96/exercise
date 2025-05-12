package org.js;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFound(ResourceNotFoundException ex) {
        ProblemDetail error = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setTitle("Validation Failed");
        problemDetail.setDetail(message);
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }

    // Handle all other exceptions (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problemDetail.setTitle("Internal Server Error");
        problemDetail.setDetail(ex.getMessage());
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", Instant.now());

        return problemDetail;
    }
}
