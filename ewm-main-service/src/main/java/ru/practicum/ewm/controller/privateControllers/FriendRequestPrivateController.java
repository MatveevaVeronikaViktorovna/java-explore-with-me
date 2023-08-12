package ru.practicum.ewm.controller.privateControllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.FriendRequestDto;
import ru.practicum.ewm.service.FriendRequestService;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/friends")
@Validated
@Slf4j
public class FriendRequestPrivateController {

    private final FriendRequestService friendrequestService;

    @PostMapping("/{friendId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendRequestDto createFriendRequest(@PathVariable Long userId,
                                                @PathVariable Long friendId) {
        log.info("Поступила заявка от пользователя с id {} на добавление в друзья пользователя с id {} ",
                userId, friendId);
        return friendrequestService.createFriendRequest(userId, friendId);
    }

}
