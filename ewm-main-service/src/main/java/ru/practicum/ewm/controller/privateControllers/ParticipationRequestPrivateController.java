package ru.practicum.ewm.controller.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateParticipationRequestEventInitiatorRequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateParticipationRequestResponse;
import ru.practicum.ewm.service.ParticipationRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Validated
@Slf4j
public class ParticipationRequestPrivateController {

    private final ParticipationRequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        log.info("Поступил запрос от пользователя с id {} на создание запроса на участие в событии с id {} ",
                userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllRequestsByEventInitiator(@PathVariable Long userId,
                                                                        @PathVariable Long eventId) {
        log.info("Поступил запрос от инициатора с id {} на получение всех запросов на участие в его событии с id {}",
                userId, eventId);
        return requestService.getAllRequestsByEventInitiator(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getAllRequestsByRequester(@PathVariable Long userId) {
        log.info("Поступил запрос от пользователя с id {} на получение всех его запросов на участие в событиях",
                userId);
        return requestService.getAllRequestsByRequester(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto updateRequestStatusByRequester(@PathVariable Long userId,
                                                                  @PathVariable Long requestId) {
        log.info("Поступил запрос на обновление статуса запроса на участие в событии с id={} от пользователя с id={}",
                requestId, userId);
        return requestService.updateRequestStatusByRequester(userId, requestId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public UpdateParticipationRequestResponse updateRequestsStatusByEventInitiator(@PathVariable Long userId,
                                                                                   @PathVariable Long eventId,
                                                                                   @Valid @RequestBody UpdateParticipationRequestEventInitiatorRequestDto requestDto) {
        log.info("Поступил запрос на обновление статуса запросов на участие в событии с id={} от инициатора данного " +
                "события с id={}", eventId, userId);
        return requestService.updateRequestStatusByEventInitiator(userId, eventId, requestDto);
    }

}