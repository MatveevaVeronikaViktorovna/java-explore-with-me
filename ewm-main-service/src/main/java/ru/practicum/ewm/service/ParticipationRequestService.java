package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateParticipationRequestEventInitiatorRequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateParticipationRequestResponse;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequestDto createRequest(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllRequestsByRequester(Long userId);

    ParticipationRequestDto updateRequestStatusByRequester(Long userId, Long requestId);

    List<ParticipationRequestDto> getAllRequestsByEventInitiator(Long userId, Long eventId);

    UpdateParticipationRequestResponse updateRequestStatusByEventInitiator(Long userId, Long eventId, UpdateParticipationRequestEventInitiatorRequestDto requestDto);

}
