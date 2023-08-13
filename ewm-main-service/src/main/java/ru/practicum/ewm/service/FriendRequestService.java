package ru.practicum.ewm.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.dto.friendRequest.FriendRequestDto;
import ru.practicum.ewm.dto.friendRequest.UpdateFriendRequestDto;

import javax.validation.Valid;
import java.util.List;

public interface FriendRequestService {

    FriendRequestDto createFriendRequest(Long requesterId, Long friendId);
    List<FriendRequestDto> getAllFriends(Long userId);
    List<FriendRequestDto> getAllOutgoingFriendRequests(Long userId);
    List<FriendRequestDto> getAllIncomingFriendRequests(Long userId);
    List<FriendRequestDto> updateIncomingFriendRequestsStatus(Long userId, UpdateFriendRequestDto requestDto);

    }
