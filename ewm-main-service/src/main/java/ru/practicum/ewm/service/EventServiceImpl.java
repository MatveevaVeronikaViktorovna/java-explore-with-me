package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.Event.EventFullDto;
import ru.practicum.ewm.dto.Event.NewEventDto;
import ru.practicum.ewm.dto.Event.StateAction;
import ru.practicum.ewm.dto.Event.UpdateEventDto;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.EventDtoMapper;
import ru.practicum.ewm.mapper.LocationDtoMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.pagination.CustomPageRequest;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
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
    private final EventDtoMapper eventDtoMapper = Mappers.getMapper(EventDtoMapper.class);
    private final LocationDtoMapper locationDtoMapper = Mappers.getMapper(LocationDtoMapper.class);

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
        return eventDtoMapper.eventToDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateByInitiator(Long userId, Long eventId, UpdateEventDto updateEventDto) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.warn("Событие с id {} не найдено", eventId);
            throw new EntityNotFoundException(String.format("Event with id=%d was not found", eventId));
        });
        if (event.getState().equals(EventState.PUBLISHED)) {
            log.warn("изменить можно только отмененные события или события в состоянии ожидания модерации");
            throw new ConditionsNotMetException("Only pending or canceled events can be changed");
        }

        if (updateEventDto.getAnnotation() != null) {
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
        if (updateEventDto.getDescription() != null) {
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
        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }

        if (updateEventDto.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
            event.setState(EventState.PENDING);
        } else {
            event.setState(EventState.CANCELED);
        }
        locationRepository.save(event.getLocation());
        Event updatedEvent = eventRepository.save(event);
        log.info("Обновлено событие c id {} на {}", eventId, updatedEvent);
        return eventDtoMapper.eventToDto(updatedEvent);
    }

}
