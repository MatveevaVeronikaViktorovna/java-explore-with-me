package ru.practicum.ewm.dto.user;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UserShortDto {
    Long id;
    String name;
}
