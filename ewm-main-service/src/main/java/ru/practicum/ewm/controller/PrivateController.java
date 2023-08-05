package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.Event.EventFullDto;
import ru.practicum.ewm.dto.Event.NewEventDto;
import ru.practicum.ewm.dto.Event.UpdateEventInitiatorRequestDto;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.UpdateParticipationRequestEventInitiatorRequestDto;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.ParticipationRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class PrivateController {

    private final EventService eventService;
    private final ParticipationRequestService requestService;

    @GetMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllEventsByInitiator(@PathVariable Long userId,
                                                      @RequestParam(defaultValue = "0") Integer from,
                                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос на получение всех событий, добавленных пользователем с id={}. " +
                "Параметры: from={}, size={}", userId, from, size);
        return eventService.getAllByInitiator(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto newEventDto) {
        log.info("Поступил запрос от пользователя с id {} на создание события {} ", userId, newEventDto);
        return eventService.create(userId, newEventDto);
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

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createParticipationRequest(@PathVariable Long userId,
                                                              @RequestParam Long eventId) {
        log.info("Поступил запрос от пользователя с id {} на создание запроса на участие в событии с id {} ",
                userId, eventId);
        return requestService.create(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllParticipationRequestsByRequester(@PathVariable Long userId) {
        log.info("Поступил запрос от пользователя с id {} на получение всех его запросов на участие в событиях", userId);
        return requestService.getAllByRequester(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto updateParticipationRequestStatusByRequester(@PathVariable Long userId,
                                                                               @PathVariable Long requestId) {
        log.info("Поступил запрос на обновление статуса запроса на участие в событии с id={} от пользователя с id={}",
                requestId, userId);
        return requestService.updateStatusByRequester(userId, requestId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllParticipationRequestsByEventInitiator(@PathVariable Long userId,
                                                                                     @PathVariable Long eventId) {
        log.info("Поступил запрос от инициатора с id {} на получение всех запросов на участие в его событии с id {}",
                userId, eventId);
        return requestService.getAllByEventInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> updateParticipationRequestsStatusByRequester(@PathVariable Long userId,
                                                                                      @PathVariable Long eventId,
                                                                                      @Valid @RequestBody UpdateParticipationRequestEventInitiatorRequestDto requestDto) {
        log.info("Поступил запрос на обновление статуса запросов на участие в событии с id={} от инициатора данного " +
                "события с id={}", eventId, userId);
        return requestService.updateStatusByEventInitiator(userId, eventId, requestDto);
    }

}
