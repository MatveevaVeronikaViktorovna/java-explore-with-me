package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.NewEventDto;

import java.util.List;

public interface EventService {

    List<EventFullDto> getAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto newEventDto);

}
