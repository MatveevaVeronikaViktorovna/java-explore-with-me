package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        StringBuilder message = new StringBuilder();
        message.append("Field: ");
        message.append(Objects.requireNonNull(e.getFieldError()).getField());
        message.append(". Error: ");
        message.append(e.getFieldError().getDefaultMessage());
        message.append(". Value: ");
        message.append(e.getFieldError().getRejectedValue());
        String asString = message.toString();
        //   "message": "Field: name. Error: must not be blank. Value: null",
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
          return new ErrorResponse("BAD_REQUEST", "Incorrectly made request.", asString, LocalDateTime.now().format(formatter));
    }

}
