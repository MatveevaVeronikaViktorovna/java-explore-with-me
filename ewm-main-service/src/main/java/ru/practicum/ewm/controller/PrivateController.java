package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventDto;
import ru.practicum.ewm.service.EventService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class PrivateController {

    private final EventService eventService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(@PathVariable Long userId,
                                @Valid @RequestBody EventDto eventDto) {
        log.info("Поступил запрос от пользователя с id {} на создание события {} ", userId, eventDto);
        return eventService.create(userId, eventDto);
    }



}
