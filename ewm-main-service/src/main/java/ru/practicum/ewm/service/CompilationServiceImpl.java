package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.ewm.pagination.CustomPageRequest;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.ParticipationRequestRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Transactional
    @Override
    public CompilationDto update(Long id, NewCompilationDto compilationDto) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> {
            log.warn("Подборка событий с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Compilation with id=%d was not found", id));
        });

        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }
        if (compilationDto.getEvents() != null) {
            Set<Long> eventsId = compilationDto.getEvents();
            Set<Event> events = eventRepository.findAllByIdIn(eventsId);
            compilation.setEvents(events);
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        log.info("Обновлена подборка событий с id {} на {}", id, updatedCompilation);
        CompilationDto dto = compilationDtoMapper.compilationToDto(updatedCompilation);
        List<EventShortDto> compilationEvents = dto.getEvents();
        for (EventShortDto event : compilationEvents) {
            Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(event.getId(), ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmedRequests);
        }
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable page = CustomPageRequest.of(from, size);

        List<Compilation> compilations;
        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, page);
        } else {
            compilations = compilationRepository.findAll(page).getContent();
        }
        List<CompilationDto> compilationsDto = compilations
                .stream()
                .map(compilationDtoMapper::compilationToDto)
                .collect(Collectors.toList());
        for (CompilationDto dto : compilationsDto) {
            List<EventShortDto> compilationEvents = dto.getEvents();
            for (EventShortDto event : compilationEvents) {
                Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(event.getId(), ParticipationRequestStatus.CONFIRMED);
                event.setConfirmedRequests(confirmedRequests);
            }
        }
        return compilationsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getById(Long id) {
        Compilation compilation = compilationRepository.findById(id).orElseThrow(() -> {
            log.warn("Подборка событий с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Compilation with id=%d was not found", id));
        });
        CompilationDto dto = compilationDtoMapper.compilationToDto(compilation);
        List<EventShortDto> compilationEvents = dto.getEvents();
        for (EventShortDto event : compilationEvents) {
            Integer confirmedRequests = requestRepository.countAllByEventIdAndStatus(event.getId(), ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(confirmedRequests);
        }
        return dto;
    }

}
