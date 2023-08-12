package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.model.Category;

@Mapper
public interface CategoryDtoMapper {
    Category dtoToCategory(CategoryDto categoryDto);

    CategoryDto categoryToDto(Category category);
}
