package ru.practicum.ewm.dto.compilation;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.dto.event.EventShortDto;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class CompilationDto {
    List<EventShortDto> events;
    Integer id;
    Boolean pinned;
    String title;
}
