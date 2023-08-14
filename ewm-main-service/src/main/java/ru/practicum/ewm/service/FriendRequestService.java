package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.friendRequest.FriendRequestDto;
import ru.practicum.ewm.dto.friendRequest.UpdateFriendRequestDto;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.util.List;

public interface FriendRequestService {

    FriendRequestDto createFriendRequest(Long requesterId, Long friendId);

    List<UserShortDto> getAllFriends(Long userId);

    List<FriendRequestDto> getAllOutgoingFriendRequests(Long userId);

    List<FriendRequestDto> getAllIncomingFriendRequests(Long userId);

    List<FriendRequestDto> updateIncomingFriendRequestsStatus(Long userId, UpdateFriendRequestDto requestDto);

    List<FriendRequestDto> updateOutgoingFriendRequestsStatus(Long userId, UpdateFriendRequestDto requestDto);

    List<FriendRequestDto> updateFriendshipStatus(Long userId, UpdateFriendRequestDto requestDto);

}
