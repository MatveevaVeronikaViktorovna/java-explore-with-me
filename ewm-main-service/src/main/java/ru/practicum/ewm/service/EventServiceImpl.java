package ru.practicum.ewm.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.HitClient;
import ru.practicum.ewm.controller.enums.EventSort;
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
import ru.practicum.ewm.model.enums.RequestStatus;
import ru.practicum.ewm.pagination.CustomPageRequest;
import ru.practicum.ewm.repository.*;
import ru.practicum.statsDto.HitRequestDto;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.*;
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
    public static final LocalDateTime START = LocalDateTime.of(2000, 1, 1, 0, 0);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
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
    public List<EventFullDto> getAllEventsByAdmin(List<Long> users,
                                                  List<EventState> states,
                                                  List<Long> categories,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Integer from,
                                                  Integer size) {
        Pageable page = CustomPageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, page);
        List<EventFullDto> eventsDto = events
                .stream()
                .map(eventDtoMapper::eventToDto)
                .collect(Collectors.toList());
        List<Long> eventsId = new ArrayList<>();
        for (EventFullDto dto : eventsDto) {
            eventsId.add(dto.getId());
        }
        Map<Long, Long> views = getViews(eventsId);
        for (EventFullDto dto : eventsDto) {
            Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(dto.getId(),
                    RequestStatus.CONFIRMED);
            dto.setConfirmedRequests(confirmedRequests);
            dto.setViews(views.getOrDefault(dto.getId(), 0L));
        }
        return eventsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAllEventsByInitiator(Long userId, Integer from, Integer size) {
        Pageable page = CustomPageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, page);
        List<EventFullDto> eventsDto = events
                .stream()
                .map(eventDtoMapper::eventToDto)
                .collect(Collectors.toList());
        List<Long> eventsId = new ArrayList<>();
        for (EventFullDto dto : eventsDto) {
            eventsId.add(dto.getId());
        }
        Map<Long, Long> views = getViews(eventsId);
        for (EventFullDto dto : eventsDto) {
            Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(dto.getId(),
                    RequestStatus.CONFIRMED);
            dto.setConfirmedRequests(confirmedRequests);
            dto.setViews(views.getOrDefault(dto.getId(), 0L));
        }
        return eventsDto;
    }


    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllEventsByUser(String text,
                                                  List<Long> categories,
                                                  Boolean paid,
                                                  LocalDateTime rangeStart,
                                                  LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable,
                                                  EventSort sort,
                                                  Integer from,
                                                  Integer size,
                                                  String uri,
                                                  String ip) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd != null && rangeEnd.isBefore(rangeStart)) {
            log.warn("RangeStart не может быть позже чем rangeEnd");
            throw new IncorrectlyMadeRequestException("RangeStart must be earlier than rangeEnd.");
        }

        sentHitToStats(uri, ip);

        Pageable page = CustomPageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                page);
        List<EventShortDto> eventsDto = events
                .stream()
                .map(eventDtoMapper::eventToShortDto)
                .collect(Collectors.toList());
        List<Long> eventsId = new ArrayList<>();
        for (EventShortDto dto : eventsDto) {
            eventsId.add(dto.getId());
        }
        Map<Long, Long> views = getViews(eventsId);
        for (EventShortDto dto : eventsDto) {
            Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(dto.getId(),
                    RequestStatus.CONFIRMED);
            dto.setConfirmedRequests(confirmedRequests);
            dto.setViews(views.getOrDefault(dto.getId(), 0L));
        }
        if (sort != null && sort.equals(EventSort.EVENT_DATE)) {
            eventsDto.sort(Comparator.comparing(EventShortDto::getEventDate));
        }
        if (sort != null && sort.equals(EventSort.VIEWS)) {
            eventsDto.sort(Comparator.comparing(EventShortDto::getViews));
        }
        return eventsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getEventsWithUserFriendsInParticipants(Long userId, Integer from, Integer size) {
        Pageable page = CustomPageRequest.of(from, size);
        List<Event> events = eventRepository.findAllWithUserFriendsInParticipants(userId, page);
        List<EventShortDto> eventsDto = events
                .stream()
                .map(eventDtoMapper::eventToShortDto)
                .collect(Collectors.toList());
        List<Long> eventsId = new ArrayList<>();
        for (EventShortDto dto : eventsDto) {
            eventsId.add(dto.getId());
        }
        Map<Long, Long> views = getViews(eventsId);
        for (EventShortDto dto : eventsDto) {
            Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(dto.getId(),
                    RequestStatus.CONFIRMED);
            dto.setConfirmedRequests(confirmedRequests);
            dto.setViews(views.getOrDefault(dto.getId(), 0L));
        }
        return eventsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByIdByInitiator(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        EventFullDto eventDto = eventDtoMapper.eventToDto(event);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        eventDto.setConfirmedRequests(confirmedRequests);
        Map<Long, Long> views = getViews(List.of(eventDto.getId()));
        eventDto.setViews(views.getOrDefault(eventDto.getId(), 0L));
        return eventDto;
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEventByIdByUser(Long eventId, String uri, String ip) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() -> {
            log.warn("Событие с id {} в статусе {} не найдено", eventId, EventState.PUBLISHED);
            throw new EntityNotFoundException(String.format("Event with id=%d with state PUBLISHED was not found",
                    eventId));
        });

        sentHitToStats(uri, ip);

        EventFullDto eventDto = eventDtoMapper.eventToDto(event);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        eventDto.setConfirmedRequests(confirmedRequests);
        Map<Long, Long> views = getViews(List.of(eventDto.getId()));
        eventDto.setViews(views.getOrDefault(eventDto.getId(), 0L));
        return eventDto;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequestDto updateEventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });

        if (updateEventDto.getAnnotation() != null && !updateEventDto.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(() -> {
                log.warn("Категория с id {} не найдена", updateEventDto.getCategory());
                throw new EntityNotFoundException(String.format("Category with id=%d was not found",
                        updateEventDto.getCategory()));
            });
            event.setCategory(category);
        }
        if (updateEventDto.getDescription() != null && !updateEventDto.getDescription().isBlank()) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getLocation() != null) {
            event.setLocation(locationDtoMapper.dtoToLocation(updateEventDto.getLocation()));
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null && !updateEventDto.getTitle().isBlank()) {
            event.setTitle(updateEventDto.getTitle());
        }

        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    log.warn("Событие можно публиковать, только если оно в состоянии ожидания публикации.");
                    throw new ConditionsNotMetException("Cannot publish the event because it's not in the right state: "
                            + event.getState());
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else {
                if (!event.getState().equals(EventState.PENDING)) {
                    log.warn("Событие можно отклонить, только если оно еще не опубликовано.");
                    throw new ConditionsNotMetException("Cannot reject the event because it's not in the right state: "
                            + event.getState());
                }
                event.setState(EventState.CANCELED);
            }
        }
        if (event.getPublishedOn() != null && event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            log.warn("Дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
            throw new ConditionsNotMetException("The event date must be no earlier than an hour from the date " +
                    "of publication.");
        }

        locationRepository.save(event.getLocation());
        log.info("Администратором обновлено событие c id {} на {}", eventId, event);
        EventFullDto eventDto = eventDtoMapper.eventToDto(event);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        eventDto.setConfirmedRequests(confirmedRequests);
        Map<Long, Long> views = getViews(List.of(eventDto.getId()));
        eventDto.setViews(views.getOrDefault(eventDto.getId(), 0L));
        return eventDto;
    }

    @Transactional
    @Override
    public EventFullDto updateEventByInitiator(Long userId,
                                               Long eventId,
                                               UpdateEventInitiatorRequestDto updateEventDto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено у инициатора с id {}", eventId, userId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (event.getState().equals(EventState.PUBLISHED)) {
            log.warn("изменить можно только отмененные события или события в состоянии ожидания модерации");
            throw new ConditionsNotMetException("Only pending or canceled events can be changed");
        }

        if (updateEventDto.getAnnotation() != null && !updateEventDto.getAnnotation().isBlank()) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }
        if (updateEventDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(() -> {
                log.warn("Категория с id {} не найдена", updateEventDto.getCategory());
                throw new EntityNotFoundException(String.format("Category with id=%d was not found",
                        updateEventDto.getCategory()));
            });
            event.setCategory(category);
        }
        if (updateEventDto.getDescription() != null && !updateEventDto.getDescription().isBlank()) {
            event.setDescription(updateEventDto.getDescription());
        }
        if (updateEventDto.getEventDate() != null) {
            event.setEventDate(updateEventDto.getEventDate());
        }
        if (updateEventDto.getLocation() != null) {
            event.setLocation(locationDtoMapper.dtoToLocation(updateEventDto.getLocation()));
        }
        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }
        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }
        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }
        if (updateEventDto.getTitle() != null && !updateEventDto.getTitle().isBlank()) {
            event.setTitle(updateEventDto.getTitle());
        }

        if (updateEventDto.getStateAction() != null) {
            if (updateEventDto.getStateAction().equals(InitiatorStateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else {
                event.setState(EventState.CANCELED);
            }
        }

        locationRepository.save(event.getLocation());
        log.info("Инициатором обновлено событие c id {} на {}", eventId, event);
        EventFullDto eventDto = eventDtoMapper.eventToDto(event);
        Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        eventDto.setConfirmedRequests(confirmedRequests);
        Map<Long, Long> views = getViews(List.of(eventDto.getId()));
        eventDto.setViews(views.getOrDefault(eventDto.getId(), 0L));
        return eventDto;
    }

    private void sentHitToStats(String uri, String ip) {
        HitRequestDto hitRequestDto = new HitRequestDto();
        hitRequestDto.setApp(APP);
        hitRequestDto.setUri(uri);
        hitRequestDto.setIp(ip);
        hitRequestDto.setTimestamp(LocalDateTime.now());
        hitClient.create(hitRequestDto);
        log.info("Информация направлена в сервер статистики: {}", hitRequestDto);
    }

    private Map<Long, Long> getViews(List<Long> eventsId) {
        List<String> uris = new ArrayList<>();
        for (Long eventId : eventsId) {
            String uri = "/events/" + eventId;
            uris.add(uri);
        }
        ResponseEntity<Object> response = hitClient.getStats(START, LocalDateTime.now(), uris, true);
        log.info("В сервер статистики направлен запрос на получение статистики за период с {} по {} для списка " +
                "uri {}, unique = {}", START, LocalDateTime.now(), uris, true);
        Object responseBody = response.getBody();
        System.out.println(responseBody);
        List<HitResponseDto> result = objectMapper.convertValue(responseBody, new TypeReference<List<HitResponseDto>>() {
        });

        Map<Long, Long> views = new HashMap<>();
        for (HitResponseDto dto : result) {
            String uri = dto.getUri();
            String[] split = uri.split("/");
            String id = split[2];
            Long eventId = Long.parseLong(id);
            views.put(eventId, dto.getHits());
        }
        return views;
    }

}
