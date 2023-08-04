package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.Event.EventFullDto;
import ru.practicum.ewm.dto.Event.NewEventDto;
import ru.practicum.ewm.dto.Event.UpdateEventAdminRequestDto;
import ru.practicum.ewm.dto.Event.UpdateEventInitiatorRequestDto;

import java.util.List;

public interface EventService {

    List<EventFullDto> getAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto newEventDto);
    EventFullDto getByIdByInitiator(Long userId, Long eventId);
    EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventInitiatorRequestDto eventDto);
    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequestDto eventDto);

}
