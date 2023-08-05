package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.dto.UpdateParticipationRequestEventInitiatorRequestDto;

import java.util.List;

public interface ParticipationRequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> getAllByRequester(Long userId);

    ParticipationRequestDto updateStatusByRequester(Long userId, Long requestId);

    List<ParticipationRequestDto> getAllByEventInitiator(Long userId, Long eventId);

    List<ParticipationRequestDto> updateStatusByEventInitiator(Long userId, Long eventId, UpdateParticipationRequestEventInitiatorRequestDto requestDto);

}
