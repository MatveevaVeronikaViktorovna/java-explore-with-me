package ru.practicum.ewm.dto.participationRequest;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.enums.ParticipationRequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UpdateParticipationRequestEventInitiatorRequestDto {
    @NotNull(message = "must not be null")
    List<Long> requestIds;

    @NotNull(message = "must not be null")
    ParticipationRequestStatus status;
}
