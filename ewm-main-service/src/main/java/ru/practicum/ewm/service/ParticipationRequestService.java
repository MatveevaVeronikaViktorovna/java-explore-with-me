package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.ParticipationRequestDto;

public interface ParticipationRequestService {

    ParticipationRequestDto create(Long userId, Long eventId);

}
