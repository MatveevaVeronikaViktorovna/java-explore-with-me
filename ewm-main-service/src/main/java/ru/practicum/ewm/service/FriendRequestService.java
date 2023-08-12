package ru.practicum.ewm.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.dto.FriendRequestDto;

public interface FriendRequestService {

    FriendRequestDto createFriendRequest(Long requesterId, Long friendId);

}
