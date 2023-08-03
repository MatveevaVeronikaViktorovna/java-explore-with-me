package ru.practicum.ewm.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@RestControllerAdvice
public class ErrorHandler {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        String message;
        if (e.hasFieldErrors()) {
            StringBuilder builder = new StringBuilder();
            for (FieldError fieldError : e.getFieldErrors()) {
                builder.append("Field: ");
                builder.append(fieldError.getField());
                builder.append(". Error: ");
                builder.append(fieldError.getDefaultMessage());
                builder.append(". Value: ");
                builder.append(fieldError.getRejectedValue());
                builder.append(". ");
            }
            message = builder.toString();
        } else {
            message = e.getMessage();
        }
        return new ApiError("BAD_REQUEST", "Incorrectly made request.", message,
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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConditionsNotMetException(final ConditionsNotMetException e) {
        return new ApiError("FORBIDDEN", "For the requested operation the conditions are not met.", e.getMessage(),
                LocalDateTime.now().format(formatter));
    }

}
