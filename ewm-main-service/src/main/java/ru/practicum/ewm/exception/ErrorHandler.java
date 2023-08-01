package ru.practicum.ewm.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@RestControllerAdvice
public class ErrorHandler {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        message.append("Field: ");
        message.append(Objects.requireNonNull(e.getFieldError()).getField());
        message.append(". Error: ");
        message.append(e.getFieldError().getDefaultMessage());
        message.append(". Value: ");
        message.append(e.getFieldError().getRejectedValue());
        String asString = message.toString();
        return new ApiError("BAD_REQUEST", "Incorrectly made request.", asString,
                LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        return new ApiError("CONFLICT", "Integrity constraint has been violated.", e.getMessage(),
                LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        return new ApiError("BAD_REQUEST", "Incorrectly made request.", e.getMessage(),
                LocalDateTime.now().format(formatter));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException e) {
        return new ApiError("NOT_FOUND", "The required object was not found.", e.getMessage(),
                LocalDateTime.now().format(formatter));
    }

}
