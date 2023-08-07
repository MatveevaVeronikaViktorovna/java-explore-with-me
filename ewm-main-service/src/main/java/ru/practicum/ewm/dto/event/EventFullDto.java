package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.enums.EventState;

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
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Long views;

}
