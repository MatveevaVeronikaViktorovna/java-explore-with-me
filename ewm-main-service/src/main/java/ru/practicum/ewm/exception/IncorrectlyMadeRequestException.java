package ru.practicum.ewm.exception;

public class IncorrectlyMadeRequestException extends RuntimeException {
    public IncorrectlyMadeRequestException(String message) {
        super(message);
    }
}
