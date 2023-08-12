package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.participationRequest.RequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateRequestEventInitiatorRequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateRequestResponse;

import java.util.List;

public interface ParticipationRequestService {

    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> getAllRequestsByRequester(Long userId);

    RequestDto updateRequestStatusByRequester(Long userId, Long requestId);

    List<RequestDto> getAllRequestsByEventInitiator(Long userId, Long eventId);

    UpdateRequestResponse updateRequestStatusByEventInitiator(Long userId,
                                                              Long eventId,
                                                              UpdateRequestEventInitiatorRequestDto requestDto);

}
