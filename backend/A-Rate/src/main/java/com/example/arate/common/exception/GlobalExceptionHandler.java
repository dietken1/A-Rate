package com.example.arate.common.exception;

import com.example.arate.lectures.exception.LectureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LectureException.LectureNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLectureNotFoundException(LectureException.LectureNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(LectureException.EvaluationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEvaluationNotFoundException(LectureException.EvaluationNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(LectureException.DuplicateEvaluationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEvaluationException(LectureException.DuplicateEvaluationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(LectureException.UnauthorizedEvaluationException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedEvaluationException(LectureException.UnauthorizedEvaluationException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(LectureException.InvalidEvaluationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEvaluationException(LectureException.InvalidEvaluationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("서버 내부 오류가 발생했습니다."));
    }

    public record ErrorResponse(String message) {}
} 