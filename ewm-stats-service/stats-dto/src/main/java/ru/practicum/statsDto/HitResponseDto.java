package ru.practicum.statsDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
public class HitResponseDto {
    String app;
    String uri;
    Long hits;
}
