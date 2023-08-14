package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.model.User;

@Mapper
public interface UserDtoMapper {
    User dtoToUser(UserDto userDto);

    UserDto userToDto(User user);

    UserShortDto userToShortDto(User user);
}
