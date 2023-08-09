package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.participationRequest.ParticipationRequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateParticipationRequestEventInitiatorRequestDto;
import ru.practicum.ewm.dto.participationRequest.UpdateParticipationRequestResponse;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.ParticipationRequestDtoMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.ParticipationRequestStatus;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
            throw new ConditionsNotMetException("The initiator of the event cannot add a request to participate in " +
                    "his event");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.warn("Нельзя участвовать в неопубликованном событии");
            throw new ConditionsNotMetException("It is not possible to participate in an unpublished event");
        }

        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                ParticipationRequestStatus.CONFIRMED);
        if (event.getParticipantLimit() <= confirmedRequests && event.getParticipantLimit() != 0) {
            log.warn("У события достигнут лимит запросов на участие.");
            throw new ConditionsNotMetException(String.format("The event has reached participant limit %d",
                    event.getParticipantLimit()));
        }

        if (event.getRequestModeration().equals(Boolean.FALSE) || event.getParticipantLimit() == 0) {
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

    @Transactional(readOnly = true)
    @Override
    public List<ParticipationRequestDto> getAllByEventInitiator(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });

        eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });

        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndEventInitiatorId(eventId, userId);
        return requests
                .stream()
                .map(requestDtoMapper::participationRequestToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto updateStatusByRequester(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findAllByIdAndRequesterId(requestId, userId).orElseThrow(() -> {
            log.warn("Запрос на участие в событии с id {} не найден у пользователя с id {}", requestId, userId);
            throw new EntityNotFoundException(String.format("Request with id=%d was not found", requestId));
        });

        request.setStatus(ParticipationRequestStatus.CANCELED);
        ParticipationRequest updatedRequest = requestRepository.save(request);
        log.info("Пользователем обновлен статус заявки на участие в событии c id {} на {}", requestId,
                ParticipationRequestStatus.CANCELED);
        return requestDtoMapper.participationRequestToDto(updatedRequest);
    }

    @Transactional
    @Override
    public UpdateParticipationRequestResponse updateStatusByEventInitiator(Long userId, Long eventId,
                                                                           UpdateParticipationRequestEventInitiatorRequestDto requestDto) {
        userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });

        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });

        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                ParticipationRequestStatus.CONFIRMED);
        int limitForConfirmation = event.getParticipantLimit() - confirmedRequests;
        if (requestDto.getStatus().equals(ParticipationRequestStatus.CONFIRMED) && limitForConfirmation <= 0) {
            log.warn("У события достигнут лимит запросов на участие.");
            throw new ConditionsNotMetException(String.format("The event has reached participant limit %d",
                    event.getParticipantLimit()));
        }

        List<ParticipationRequest> requests = requestRepository.findAllByEventIdAndEventInitiatorIdAndIdIn(eventId,
                userId, requestDto.getRequestIds());

        if (requestDto.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
            for (ParticipationRequest request : requests) {
                if (!request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                    log.warn("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
                    throw new ConditionsNotMetException("Request cannot be confirmed because it's not in the right " +
                            "status: " + request.getStatus());
                }
                if (limitForConfirmation > 0) {
                    request.setStatus(ParticipationRequestStatus.CONFIRMED);
                    limitForConfirmation--;
                } else {
                    request.setStatus(ParticipationRequestStatus.REJECTED);
                }
            }
        } else if (requestDto.getStatus().equals(ParticipationRequestStatus.REJECTED)) {
            for (ParticipationRequest request : requests) {
                if (!request.getStatus().equals(ParticipationRequestStatus.PENDING)) {
                    log.warn("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
                    throw new ConditionsNotMetException("Request cannot be confirmed because it's not in the right " +
                            "status: " + request.getStatus());
                }
                request.setStatus(ParticipationRequestStatus.REJECTED);
            }
        } else {
            log.warn("Новый статус для заявок на участие в событии текущего пользователя должен быть CONFIRMED" +
                    " or REJECTED");
            throw new ConditionsNotMetException("New status of participation requests must be CONFIRMED or REJECTED");
        }

        List<ParticipationRequest> updatedRequests = requestRepository.saveAll(requests);
        log.info("Инициатором с id {} обновлен статус заявок на участие в событии c id {} на {}", userId, eventId,
                updatedRequests);

        List<ParticipationRequestDto> requestsDto = requests
                .stream()
                .map(requestDtoMapper::participationRequestToDto)
                .collect(Collectors.toList());
        List<ParticipationRequestDto> confirmedRequestsDto = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequestsDto = new ArrayList<>();
        for (ParticipationRequestDto dto : requestsDto) {
            if (dto.getStatus().equals(ParticipationRequestStatus.CONFIRMED)) {
                confirmedRequestsDto.add(dto);
            } else {
                rejectedRequestsDto.add(dto);
            }
        }
        UpdateParticipationRequestResponse response = new UpdateParticipationRequestResponse();
        response.setConfirmedRequests(confirmedRequestsDto);
        response.setRejectedRequests(rejectedRequestsDto);
        return response;
    }

}
