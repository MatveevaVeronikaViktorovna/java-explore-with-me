package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.User;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class EventDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotBlank(message = "must not be blank")
    @Size(min = 20, max = 2000)
    String annotation;
    @NotBlank(message = "must not be blank")
    Long category;
    @NotNull(message = "must not be null")
    @Size(min = 20, max = 7000)
    String description;
    @NotNull(message = "must not be null")
    @JsonFormat(pattern = DATE_TIME_FORMAT)
    LocalDateTime eventDate;
    @NotNull(message = "must not be null")
    Location location;

    Boolean paid;
    Integer participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    State state;
    String title;
    Long views;

}
