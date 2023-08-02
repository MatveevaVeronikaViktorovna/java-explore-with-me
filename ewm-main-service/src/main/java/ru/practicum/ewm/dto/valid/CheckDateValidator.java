package ru.practicum.ewm.dto.valid;

import ru.practicum.ewm.dto.NewEventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartTwoHoursAfterNowDateValid, NewEventDto> {

    @Override
    public void initialize(StartTwoHoursAfterNowDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(NewEventDto newEventDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime eventDate = newEventDto.getEventDate();
        LocalDateTime now = LocalDateTime.now();
        return eventDate.isAfter(now.plusHours(2L));
    }
}
