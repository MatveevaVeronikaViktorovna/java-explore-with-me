package ru.practicum.ewm.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;

import javax.validation.Valid;

public interface CompilationService {

    CompilationDto create(NewCompilationDto compilationDto);

    void delete(Long compId);
    CompilationDto update(Long compId,NewCompilationDto compilationDto);
}
