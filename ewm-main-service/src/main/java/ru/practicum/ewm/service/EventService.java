package ru.practicum.ewm.service;

import ru.practicum.ewm.controller.EventSort;
import ru.practicum.ewm.dto.event.*;
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

    List<EventShortDto> getAllByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from, Integer size);

}
