package ru.practicum.ewm.dto.friendRequest;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class FriendRequestDto {

    Long id;
    Long requester;
    Long friend;
    RequestStatus status;
    LocalDateTime created;

}
