package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.Event.EventFullDto;
import ru.practicum.ewm.dto.Event.InitiatorStateAction;
import ru.practicum.ewm.dto.Event.UpdateEventInitiatorRequestDto;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.ParticipationRequestDtoMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
        if (event.getParticipantLimit() <= confirmedRequests) {
            log.warn("У события достигнут лимит запросов на участие.");
            throw new ConditionsNotMetException(String.format("The event has reached participant limit %d", event.getParticipantLimit()));
        }

        if (event.getRequestModeration().equals(Boolean.FALSE)) {
            request.setStatus(ParticipationRequestStatus.CONFIRMED);
        } else {
            request.setStatus(ParticipationRequestStatus.PENDING);
        }

        request.setCreated(LocalDateTime.now());

        ParticipationRequest newRequest = requestRepository.save(request);
        log.info("Добавлен запрос на участие в событии: {}", newRequest);
        return requestDtoMapper.participationRequestToDto(newRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getAllByRequester(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });

        List<ParticipationRequest> requests = requestRepository.findAllByRequesterId(userId);
        return requests
                .stream()
                .map(requestDtoMapper::participationRequestToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto updateStatusByRequester(Long userId, Long requestId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено у инициатора с id {}", eventId, userId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (event.getState().equals(EventState.PUBLISHED)) {
            log.warn("изменить можно только отмененные события или события в состоянии ожидания модерации");
            throw new ConditionsNotMetException("Only pending or canceled events can be changed");
        }

        if (updateEventInitiatorRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventInitiatorRequestDto.getAnnotation());
        }
        if (updateEventInitiatorRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventInitiatorRequestDto.getCategory()).orElseThrow(() -> {
                log.warn("Категория с id {} не найдена", updateEventInitiatorRequestDto.getCategory());
                throw new EntityNotFoundException(String.format("Category with id=%d was not found",
                        updateEventInitiatorRequestDto.getCategory()));
            });
            event.setCategory(category);
        }
        if (updateEventInitiatorRequestDto.getDescription() != null) {
            event.setDescription(updateEventInitiatorRequestDto.getDescription());
        }
        if (updateEventInitiatorRequestDto.getEventDate() != null) {
            event.setEventDate(updateEventInitiatorRequestDto.getEventDate());
        }
        if (updateEventInitiatorRequestDto.getLocation() != null) {
            event.setLocation(locationDtoMapper.dtoToLocation(updateEventInitiatorRequestDto.getLocation()));
        }
        if (updateEventInitiatorRequestDto.getPaid() != null) {
            event.setPaid(updateEventInitiatorRequestDto.getPaid());
        }
        if (updateEventInitiatorRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventInitiatorRequestDto.getParticipantLimit());
        }
        if (updateEventInitiatorRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventInitiatorRequestDto.getRequestModeration());
        }
        if (updateEventInitiatorRequestDto.getTitle() != null) {
            event.setTitle(updateEventInitiatorRequestDto.getTitle());
        }

        if (updateEventInitiatorRequestDto.getStateAction() != null) {
            if (updateEventInitiatorRequestDto.getStateAction().equals(InitiatorStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        locationRepository.save(event.getLocation());
        Event updatedEvent = eventRepository.save(event);
        log.info("Инициатором обновлено событие c id {} на {}", eventId, updatedEvent);
        return eventDtoMapper.eventToDto(updatedEvent);
    }

}
