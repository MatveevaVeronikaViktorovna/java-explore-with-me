package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.event.valid.StartTwoHoursAfterNow;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class NewEventDto {

    @NotBlank(message = "must not be blank")
    @Size(min = 20, max = 2000)
    String annotation;

    @NotNull(message = "must not be null")
    Long category;

    @NotBlank(message = "must not be blank")
    @Size(min = 20, max = 7000)
    String description;

    @NotNull(message = "must not be null")
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    @StartTwoHoursAfterNow
    LocalDateTime eventDate;

    @NotNull(message = "must not be null")
    LocationDto location;

    Boolean paid;
    Integer participantLimit;
    Boolean requestModeration;

    @NotBlank(message = "must not be blank")
    @Size(min = 3, max = 120)
    String title;

}
