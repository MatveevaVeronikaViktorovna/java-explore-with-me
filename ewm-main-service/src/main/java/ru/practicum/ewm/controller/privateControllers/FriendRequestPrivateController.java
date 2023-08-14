package ru.practicum.ewm.controller.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.friendRequest.FriendRequestDto;
import ru.practicum.ewm.dto.friendRequest.UpdateFriendRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.FriendRequestService;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/friends")
@Validated
@Slf4j
public class FriendRequestPrivateController {

    private final FriendRequestService friendRequestService;
    private final EventService eventService;
    private final UserService userService;

    @PostMapping("/{friendId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendRequestDto createFriendRequest(@PathVariable Long userId,
                                                @PathVariable Long friendId) {
        log.info("Поступила заявка от пользователя с id {} на добавление в друзья пользователя с id {} ",
                userId, friendId);
        return friendRequestService.createFriendRequest(userId, friendId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserShortDto> getUserFriends(@PathVariable Long userId) {
        log.info("Поступил запрос от пользователя с id {} на получение списка его друзей", userId);
        return userService.getUserFriends(userId);
    }

    @GetMapping("/requests/incoming")
    @ResponseStatus(HttpStatus.OK)
    public List<FriendRequestDto> getAllIncomingFriendRequests(@PathVariable Long userId) {
        log.info("Поступил запрос от пользователя с id {} на получение его входящих заявок в друзья", userId);
        return friendRequestService.getAllIncomingFriendRequests(userId);
    }

    @GetMapping("/requests/outgoing")
    @ResponseStatus(HttpStatus.OK)
    public List<FriendRequestDto> getAllOutgoingFriendRequests(@PathVariable Long userId) {
        log.info("Поступил запрос от пользователя с id {} на получение его исходящих заявок в друзья", userId);
        return friendRequestService.getAllOutgoingFriendRequests(userId);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public List<FriendRequestDto> updateFriendshipStatus(@PathVariable Long userId,
                                                         @Valid @RequestBody UpdateFriendRequestDto requestDto) {
        log.info("Поступил запрос на обновление статуса подтвержденных заявок в друзья от пользователя с id={}", userId);
        return friendRequestService.updateFriendshipStatus(userId, requestDto);
    }

    @PatchMapping("/requests/incoming")
    @ResponseStatus(HttpStatus.OK)
    public List<FriendRequestDto> updateIncomingFriendRequestsStatus(@PathVariable Long userId,
                                                                     @Valid @RequestBody UpdateFriendRequestDto requestDto) {
        log.info("Поступил запрос на обновление статуса входящих заявок в друзья от пользователя с id={}", userId);
        return friendRequestService.updateIncomingFriendRequestsStatus(userId, requestDto);
    }

    @PatchMapping("/requests/outgoing")
    @ResponseStatus(HttpStatus.OK)
    public List<FriendRequestDto> updateOutgoingFriendRequestsStatus(@PathVariable Long userId,
                                                                     @Valid @RequestBody UpdateFriendRequestDto requestDto) {
        log.info("Поступил запрос на обновление статуса исходящих заявок в друзья от пользователя с id={}", userId);
        return friendRequestService.updateOutgoingFriendRequestsStatus(userId, requestDto);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsWithUserFriendsInParticipants(@PathVariable Long userId,
                                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                                      @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Поступил запрос от пользователя с id {} на получение списка актуальных событий, в которых его" +
                " друзья принимают участие. Параметры: from={}, size={}", userId, from, size);
        return eventService.getEventsWithUserFriendsInParticipants(userId, from, size);
    }

}
