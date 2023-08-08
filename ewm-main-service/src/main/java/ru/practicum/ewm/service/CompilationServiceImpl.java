package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.mapper.CompilationDtoMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
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
        return compilationDtoMapper.compilationToDto(newCompilation);
    }

}
