package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class EventFullDto {

    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Long views;

}
