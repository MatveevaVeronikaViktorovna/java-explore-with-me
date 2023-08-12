package ru.practicum.ewm.dto.participationRequest;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class UpdateRequestResponse {
    List<RequestDto> confirmedRequests;
    List<RequestDto> rejectedRequests;
}
