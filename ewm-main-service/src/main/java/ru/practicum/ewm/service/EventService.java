package ru.practicum.ewm.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.dto.Event.EventFullDto;
import ru.practicum.ewm.dto.Event.NewEventDto;
import ru.practicum.ewm.dto.Event.UpdateEventDto;

import javax.validation.Valid;
import java.util.List;

public interface EventService {

    List<EventFullDto> getAllByInitiator(Long userId, Integer from, Integer size);

    EventFullDto create(Long userId, NewEventDto newEventDto);
    EventFullDto getByIdByInitiator(Long userId, Long eventId);
    EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventDto updateEventDto);

}
