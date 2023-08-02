package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.NewEventDto;

public interface EventService {

    NewEventDto create(Long userId, NewEventDto newEventDto);

}
