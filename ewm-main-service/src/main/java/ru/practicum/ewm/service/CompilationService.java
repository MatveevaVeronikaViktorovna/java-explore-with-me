package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;

public interface CompilationService {

    CompilationDto create(NewCompilationDto compilationDto);
}
