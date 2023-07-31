package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.model.User;

@Mapper
public interface UserDtoMapper {
    User dtoToUser(UserDto userDto);
    UserDto userToDto(User user);
}
