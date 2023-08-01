package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);
    void delete(Long id);
    List<UserDto> getAll();



}
