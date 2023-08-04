package ru.practicum.ewm.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.ParticipationRequestStatus;

import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class ParticipationRequestDto {

    LocalDateTime created;
    Long event;
    Long id;
    Long requester;
    ParticipationRequestStatus status;

}
