package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.EventDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.EventDtoMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.LocationRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final EventDtoMapper eventDtoMapper = Mappers.getMapper(EventDtoMapper.class);

    @Override
    public EventDto create(Long userId, EventDto eventDto) {
        Event event = eventDtoMapper.dtoToEvent(eventDto);
        locationRepository.save(event.getLocation());

        Long catId = eventDto.getCategory();
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

}
