package ru.yandex.practicum.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn("Ошибка валидации параметров запроса: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse("Ошибка валидации", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Ошибка валидации параметров запроса: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse("Ошибка валидации", ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        log.warn("Некорректный формат JSON: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorResponse("Ошибка валидации",
                        "Некорректный формат тела запроса (ожидается корректный JSON)."),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(MissingServletRequestParameterException ex) {
        log.warn("Отсутствует обязательный параметр: {}", ex.getParameterName());
        return new ResponseEntity<>(
                new ErrorResponse("Ошибка валидации",
                        "Отсутствует обязательный параметр: " + ex.getParameterName()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("Неподдерживаемый HTTP-метод: {}", ex.getMethod());
        return new ResponseEntity<>(
                new ErrorResponse("HTTP-метод не поддерживается",
                        "HTTP-метод не поддерживается: " + ex.getMethod()),
                HttpStatus.METHOD_NOT_ALLOWED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnknown(Exception ex) {
        log.error("Неизвестная ошибка в gateway", ex);
        return new ResponseEntity<>(
                new ErrorResponse("Исключение со стороны сервера",
                        "Внутренняя ошибка шлюза. Пожалуйста, попробуйте позже."),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
