package ru.practicum.ewm.dto.participationRequest;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.enums.ParticipationRequestStatus;

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
