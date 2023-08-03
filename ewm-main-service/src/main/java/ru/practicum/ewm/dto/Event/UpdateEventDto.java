package ru.practicum.ewm.dto.Event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.Event.valid.StartTwoHoursAfterNow;
import ru.practicum.ewm.dto.LocationDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UpdateEventDto {

    @Size(min = 20, max = 2000)
    String annotation;

    Long category;

    @Size(min = 20, max = 7000)
    String description;

    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @StartTwoHoursAfterNow
    LocalDateTime eventDate;

    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;

    StateAction stateAction;
    
    @Size(min = 3, max = 120)
    String title;
}
