package ru.practicum.ewm.service;

import ru.practicum.ewm.controller.enums.EventSort;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.model.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> getAllEventsByInitiator(Long userId, Integer from, Integer size);

    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByIdByInitiator(Long userId, Long eventId);

    EventFullDto updateEventByInitiator(Long userId, Long eventId, UpdateEventInitiatorRequestDto eventDto);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto eventDto);

    List<EventFullDto> getAllEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventShortDto> getAllEventsByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from, Integer size, String uri, String api);

    EventFullDto getEventByIdByUser(Long eventId, String uri, String ip);
}
