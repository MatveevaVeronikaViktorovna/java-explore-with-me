package ru.practicum.ewm.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.enums.RequestStatus;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class FriendRequestDto {

    Long id;
    Long friend;
    Long requester;
    RequestStatus status;
    LocalDateTime created;

}
