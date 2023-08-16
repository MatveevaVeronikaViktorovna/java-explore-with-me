package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.friendRequest.FriendRequestDto;
import ru.practicum.ewm.model.FriendRequest;
import ru.practicum.ewm.model.User;

@Mapper(componentModel = "spring")
public interface FriendRequestDtoMapper {

    FriendRequestDto friendRequestToDto(FriendRequest request);

    default Long mapUserToLong(User user) {
        return user.getId();
    }

}
