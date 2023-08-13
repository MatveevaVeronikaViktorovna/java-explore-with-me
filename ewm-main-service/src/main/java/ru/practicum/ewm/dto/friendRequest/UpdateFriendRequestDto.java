package ru.practicum.ewm.dto.friendRequest;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.model.enums.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UpdateFriendRequestDto {

    @NotNull(message = "must not be null")
    List<Long> requestIds;

    @NotNull(message = "must not be null")
    RequestStatus status;

}
