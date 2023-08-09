package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.HitClient;
import ru.practicum.ewm.controller.EventSort;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.event.enums.AdminStateAction;
import ru.practicum.ewm.dto.event.enums.InitiatorStateAction;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.exception.IncorrectlyMadeRequestException;
import ru.practicum.ewm.mapper.EventDtoMapper;
import ru.practicum.ewm.mapper.LocationDtoMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.model.enums.ParticipationRequestStatus;
import ru.practicum.ewm.pagination.CustomPageRequest;
import ru.practicum.ewm.repository.*;
import ru.practicum.statsDto.HitRequestDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventDtoMapper eventDtoMapper = Mappers.getMapper(EventDtoMapper.class);
    private final LocationDtoMapper locationDtoMapper = Mappers.getMapper(LocationDtoMapper.class);
    private final HitClient hitClient;
    public static final String APP = "ewm-main-service";

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAllByInitiator(Long userId, Integer from, Integer size) {
        Pageable page = CustomPageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        return events
                .stream()
                .map(eventDtoMapper::eventToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
        Event event = eventDtoMapper.dtoToEvent(newEventDto);
        locationRepository.save(event.getLocation());

        Long catId = newEventDto.getCategory();
        Category category = categoryRepository.findById(catId).orElseThrow(() -> {
            log.warn("Категория с id {} не найдена", catId);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", catId));
        });
        event.setCategory(category);

        User initiator = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Пользователь с id {} не найден", userId);
            throw new EntityNotFoundException(String.format("User with id=%d was not found", userId));
        });
        event.setInitiator(initiator);

        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        Event newEvent = eventRepository.save(event);
        log.info("Добавлено событие: {}", newEvent);
        return eventDtoMapper.eventToDto(newEvent);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getByIdByInitiator(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        EventFullDto eventDto = eventDtoMapper.eventToDto(event);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
        eventDto.setConfirmedRequests(confirmedRequests);
        return eventDto;
    }

    @Transactional
    @Override
    public EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventInitiatorRequestDto updateEventInitiatorRequestDto) {
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
        EventFullDto eventDto = eventDtoMapper.eventToDto(updatedEvent);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
        eventDto.setConfirmedRequests(confirmedRequests);
        return eventDto;
    }

    @Transactional
    @Override
    public EventFullDto updateByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventAdminRequestDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });

        if (updateEventAdminRequestDto.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequestDto.getAnnotation());
        }
        if (updateEventAdminRequestDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequestDto.getCategory()).orElseThrow(() -> {
                log.warn("Категория с id {} не найдена", updateEventAdminRequestDto.getCategory());
                throw new EntityNotFoundException(String.format("Category with id=%d was not found",
                        updateEventAdminRequestDto.getCategory()));
            });
            event.setCategory(category);
        }
        if (updateEventAdminRequestDto.getDescription() != null) {
            event.setDescription(updateEventAdminRequestDto.getDescription());
        }
        if (updateEventAdminRequestDto.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequestDto.getEventDate());
        }
        if (updateEventAdminRequestDto.getLocation() != null) {
            event.setLocation(locationDtoMapper.dtoToLocation(updateEventAdminRequestDto.getLocation()));
        }
        if (updateEventAdminRequestDto.getPaid() != null) {
            event.setPaid(updateEventAdminRequestDto.getPaid());
        }
        if (updateEventAdminRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequestDto.getParticipantLimit());
        }
        if (updateEventAdminRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequestDto.getRequestModeration());
        }
        if (updateEventAdminRequestDto.getTitle() != null) {
            event.setTitle(updateEventAdminRequestDto.getTitle());
        }

        if (updateEventAdminRequestDto.getStateAction() != null) {
            if (updateEventAdminRequestDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    log.warn("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
                    throw new ConditionsNotMetException("Cannot publish the event because it's not in the right state: " + event.getState());
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                if (!event.getState().equals(EventState.PENDING)) {
                    log.warn("Событие можно отклонить, только если оно еще не опубликовано.");
                    throw new ConditionsNotMetException("Cannot reject the event because it's not in the right state: " + event.getState());
                }
                event.setState(EventState.CANCELED);
            }
        }
        if (event.getPublishedOn() != null && event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            log.warn("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
            throw new ConditionsNotMetException("The event date must be no earlier than an hour from the date of publication.");
        }
        locationRepository.save(event.getLocation());
        Event updatedEvent = eventRepository.save(event);
        log.info("Администратором обновлено событие c id {} на {}", eventId, updatedEvent);
        EventFullDto eventDto = eventDtoMapper.eventToDto(updatedEvent);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
        eventDto.setConfirmedRequests(confirmedRequests);
        return eventDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable page = CustomPageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, page);
        List<EventFullDto> eventsDto = events
                .stream()
                .map(eventDtoMapper::eventToDto)
                .collect(Collectors.toList());
        for (EventFullDto dto : eventsDto) {
            Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(dto.getId(), ParticipationRequestStatus.CONFIRMED);
            dto.setConfirmedRequests(confirmedRequests);
        }
        return eventsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from, Integer size, String uri, String ip) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            log.warn("RangeStart не может быть позже чем rangeEnd");
            throw new IncorrectlyMadeRequestException("RangeStart must be earlier than rangeEnd.");
        }

        sentHitToStats(uri, ip);

        Pageable page = CustomPageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, page);
        List<EventShortDto> eventsDto = events
                .stream()
                .map(eventDtoMapper::eventToShortDto)
                .collect(Collectors.toList());
        for (EventShortDto dto : eventsDto) {
            Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(dto.getId(), ParticipationRequestStatus.CONFIRMED);
            dto.setConfirmedRequests(confirmedRequests);
        }
        if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            eventsDto.sort(Comparator.comparing(EventShortDto::getEventDate));
        }
        // TODO надо присвоить views и сортировку сделать
        return eventsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getByIdByUser(Long eventId, String uri, String ip) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() -> {
            log.warn("Событие с id {} в статусе {} не найдено", eventId, EventState.PUBLISHED);
            throw new EntityNotFoundException(String.format("Event with id=%d with state PUBLISHED was not found", eventId));
        });

        sentHitToStats(uri, ip);

        EventFullDto eventDto = eventDtoMapper.eventToDto(event);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId, ParticipationRequestStatus.CONFIRMED);
        eventDto.setConfirmedRequests(confirmedRequests);
        return eventDto;
    }

    private void sentHitToStats (String uri, String ip) {
        HitRequestDto hitRequestDto = new HitRequestDto();
        hitRequestDto.setApp(APP);
        hitRequestDto.setUri(uri);
        hitRequestDto.setIp(ip);
        hitRequestDto.setTimestamp(LocalDateTime.now());
        hitClient.create(hitRequestDto);
        log.info("Информация направлена в сервер статистики: {}", hitRequestDto);

    }

}
