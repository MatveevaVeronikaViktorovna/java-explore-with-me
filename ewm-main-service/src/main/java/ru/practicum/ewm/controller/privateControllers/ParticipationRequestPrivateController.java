package ru.practicum.ewm.controller.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.participationRequest.RequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateRequestEventInitiatorRequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateRequestResponse;
import ru.practicum.ewm.service.ParticipationRequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}")
@Validated
@Slf4j
public class ParticipationRequestPrivateController {

    private final ParticipationRequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        log.info("Поступил запрос от пользователя с id {} на создание запроса на участие в событии с id {} ",
                userId, eventId);
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getAllRequestsByEventInitiator(@PathVariable Long userId,
                                                           @PathVariable Long eventId) {
        log.info("Поступил запрос от инициатора с id {} на получение всех запросов на участие в его событии с id {}",
                userId, eventId);
        return requestService.getAllRequestsByEventInitiator(userId, eventId);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<RequestDto> getAllRequestsByRequester(@PathVariable Long userId) {
        log.info("Поступил запрос от пользователя с id {} на получение всех его запросов на участие в событиях",
                userId);
        return requestService.getAllRequestsByRequester(userId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public RequestDto updateRequestStatusByRequester(@PathVariable Long userId,
                                                     @PathVariable Long requestId) {
        log.info("Поступил запрос на обновление статуса запроса на участие в событии с id={} от пользователя с id={}",
                requestId, userId);
        return requestService.updateRequestStatusByRequester(userId, requestId);
    }

    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public UpdateRequestResponse updateRequestsStatusByEventInitiator(@PathVariable Long userId,
                                                                      @PathVariable Long eventId,
                                                                      @Valid @RequestBody UpdateRequestEventInitiatorRequestDto requestDto) {
        log.info("Поступил запрос на обновление статуса запросов на участие в событии с id={} от инициатора данного " +
                "события с id={}", eventId, userId);
        return requestService.updateRequestStatusByEventInitiator(userId, eventId, requestDto);
    }

}
