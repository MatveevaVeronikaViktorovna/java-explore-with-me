package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.ParticipationRequestDtoMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ParticipationRequestServiceImpl implements ParticipationRequestService {

    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestDtoMapper requestDtoMapper = Mappers.getMapper(ParticipationRequestDtoMapper.class);

    @Transactional
    @Override
    public ParticipationRequestDto create(Long userId, Long eventId) {

        ParticipationRequest request = new ParticipationRequest();

        User requester = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });
        request.setRequester(requester);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        request.setEvent(event);

        if (event.getInitiator().getId().equals(userId)) {
            log.warn("Инициатор события не может добавить запрос на участие в своём событии");
            throw new ConditionsNotMetException("The initiator of the event cannot add a request to participate in his event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.warn("Нельзя участвовать в неопубликованном событии");
            throw new ConditionsNotMetException("It is not possible to participate in an unpublished event");
        }
        if(event.getRequestModeration().equals(Boolean.FALSE)) {
            request.setStatus(ParticipationRequestStatus.CONFIRMED);
        } else {
            request.setStatus(ParticipationRequestStatus.PENDING);
        }

        request.setCreated(LocalDateTime.now());

        ParticipationRequest newRequest = requestRepository.save(request);
        log.info("Добавлен запрос на участие в событии: {}", newRequest);
        return requestDtoMapper.participationRequestToDto(newRequest);

    }
}
