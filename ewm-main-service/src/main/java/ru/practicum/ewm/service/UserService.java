package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    void deleteUser(Long id);

    List<UserDto> getAllUsers(List<Long> ids, Integer from, Integer size);

    List<UserShortDto> getUserFriends(Long userId);

    List<UserShortDto> removeFriendFromUserFriends(Long userId, Long friendId);

}
