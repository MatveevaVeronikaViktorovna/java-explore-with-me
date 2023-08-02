package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventDto;

public interface EventService {

    EventDto create(Long userId, EventDto eventDto);

}
