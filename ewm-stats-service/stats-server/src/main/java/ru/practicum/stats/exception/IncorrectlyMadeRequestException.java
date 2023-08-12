package ru.practicum.stats.exception;

public class IncorrectlyMadeRequestException extends RuntimeException {
    public IncorrectlyMadeRequestException(String message) {
        super(message);
    }
}
