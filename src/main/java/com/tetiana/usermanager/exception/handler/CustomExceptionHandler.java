package com.tetiana.usermanager.exception.handler;

import com.tetiana.usermanager.dto.ErrorResponseDto;
import com.tetiana.usermanager.exception.IncorrectDateRangeException;
import com.tetiana.usermanager.exception.NotFoundException;
import com.tetiana.usermanager.exception.UserUnderAgeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException
            (MethodArgumentNotValidException exception, ServletWebRequest servletWebRequest) {
        return new ResponseEntity<>(new ErrorResponseDto(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getBindingResult().getFieldErrors()
                        .stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()),
                servletWebRequest.getRequest().getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserUnderAgeException.class)
    public ResponseEntity<ErrorResponseDto> handleUserUnderAgeException
            (UserUnderAgeException exception, ServletWebRequest servletWebRequest) {
        return new ResponseEntity<>(new ErrorResponseDto(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                List.of(exception.getMessage()),
                servletWebRequest.getRequest().getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncorrectDateRangeException.class)
    public ResponseEntity<ErrorResponseDto> handleIncorrectDateRangeException
            (IncorrectDateRangeException exception, ServletWebRequest servletWebRequest) {
        return new ResponseEntity<>(new ErrorResponseDto(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                List.of(exception.getMessage()),
                servletWebRequest.getRequest().getRequestURI()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException
            (NotFoundException exception, ServletWebRequest servletWebRequest) {
        return new ResponseEntity<>(new ErrorResponseDto(LocalDateTime.now(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                List.of(exception.getMessage()),
                servletWebRequest.getRequest().getRequestURI()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException
            (Exception exception, ServletWebRequest servletWebRequest) {
        return new ResponseEntity<>(new ErrorResponseDto(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                List.of(exception.getMessage()),
                servletWebRequest.getRequest().getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
