package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.CompilationDtoMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.enums.ParticipationRequestStatus;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository requestRepository;
    private final CompilationDtoMapper compilationDtoMapper = Mappers.getMapper(CompilationDtoMapper.class);

    @Transactional
    @Override
    public CompilationDto create(NewCompilationDto compilationDto) {
        Compilation compilation = compilationDtoMapper.dtoToCompilation(compilationDto);

        Set<Long> eventsId = compilationDto.getEvents();
        Set<Event> events = eventRepository.findAllByIdIn(eventsId);
        compilation.setEvents(events);

        Compilation newCompilation = compilationRepository.save(compilation);
        log.info("Добавлена подборка событий: {}", newCompilation);
        CompilationDto dto = compilationDtoMapper.compilationToDto(newCompilation);
        List<EventShortDto> compilationEvents = dto.getEvents();
        for (EventShortDto event : compilationEvents) {
            Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(event.getId(), ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmedRequests);
        }
        return dto;
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!compilationRepository.existsById(id)) {
            log.warn("Подборка событий с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Compilation with id=%d was not found", id));
        }

        compilationRepository.deleteById(id);
        log.info("Удалена подборка событий с id {}", id);
    }

}