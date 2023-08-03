package ru.practicum.ewm.dto.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class EventDateValidator implements ConstraintValidator<StartTwoHoursAfterNow, LocalDateTime> {

    @Override
    public void initialize(StartTwoHoursAfterNow constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime eventDate, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime now = LocalDateTime.now();
        return eventDate.isAfter(now.plusHours(2L));
    }
}
