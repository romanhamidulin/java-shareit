package ru.practicum.shareit.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException exc) {
        log.warn(exc.getMessage(), exc);
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        exc.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException exc) {
        log.warn(exc.getMessage(), exc);
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        exc.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        log.warn(exc.getMessage(), exc);
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        exc.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exc) {
        log.warn(exc.getMessage(), exc);
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        exc.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ExistException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ExistException exc) {
        log.warn(exc.getMessage(), exc);
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        exc.getMessage()),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknownException(Exception exc) {
        log.warn(exc.getMessage(), exc);
        return new ResponseEntity<>(
                new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        exc.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}