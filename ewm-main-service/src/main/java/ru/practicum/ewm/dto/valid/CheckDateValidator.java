package ru.practicum.ewm.dto.valid;

import ru.practicum.ewm.dto.EventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<StartTwoHoursAfterNowDateValid, EventDto> {

    @Override
    public void initialize(StartTwoHoursAfterNowDateValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(EventDto eventDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime eventDate = eventDto.getEventDate();
        LocalDateTime now = LocalDateTime.now();
        return eventDate.isAfter(now.plusHours(2L));
    }
}
