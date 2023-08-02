package ru.practicum.ewm.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class LocationDto {

    @NotNull(message = "must not be null")
    Float lat;

    @NotNull(message = "must not be null")
    Float lon;

}
