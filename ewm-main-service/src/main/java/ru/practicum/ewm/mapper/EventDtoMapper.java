package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.EventDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;

@Mapper
public interface EventDtoMapper {
    Event dtoToEvent(EventDto eventDto);
    EventDto eventToDto(Event event);
}
