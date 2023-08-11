package ru.practicum.ewm.controller.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.dto.event.UpdateEventInitiatorRequestDto;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Поступил запрос от пользователя с id {} на создание события {} ", userId, newEventDto);
        return eventService.create(userId, newEventDto);
    }

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllEventsByInitiator(@PathVariable Long userId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поступил запрос на получение всех событий, добавленных пользователем с id={}. " +
                "Параметры: from={}, size={}", userId, from, size);
        return eventService.getAllByInitiator(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdByInitiator(@PathVariable Long userId,
                                                @PathVariable Long eventId) {
        log.info("Поступил запрос от инициатора с id={} на получение события с id={} ", userId, eventId);
        return eventService.getByIdByInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByInitiator(@PathVariable Long userId,
                                               @PathVariable Long eventId,
                                               @Valid @RequestBody UpdateEventInitiatorRequestDto eventDto) {
        log.info("Поступил запрос на обновление события с id={} от инициатора с id={} на {}",
                eventId, userId, eventDto);
        return eventService.updateByInitiator(userId, eventId, eventDto);
    }

}
