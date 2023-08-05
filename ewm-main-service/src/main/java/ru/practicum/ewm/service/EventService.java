package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequestDto;
import ru.practicum.ewm.dto.event.UpdateEventInitiatorRequestDto;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto getByIdByInitiator(Long userId, Long eventId);

    EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventInitiatorRequestDto eventDto);

    EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequestDto eventDto);

    List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

}
