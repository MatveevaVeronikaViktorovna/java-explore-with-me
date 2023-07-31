package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    List<UserDto> getAll();

    void delete(Long id);

}
