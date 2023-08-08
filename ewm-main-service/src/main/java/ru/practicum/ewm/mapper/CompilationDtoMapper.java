package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Compilation;

@Mapper
public interface CompilationDtoMapper {

    @Mapping(target = "events", ignore = true)
    Compilation dtoToCompilation(NewCompilationDto newCompilationDto);

    CompilationDto compilationToDto(Compilation compilation);

}
