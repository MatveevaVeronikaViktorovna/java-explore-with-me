package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);

    void delete(Long id);

    CategoryDto update(Long id, CategoryDto categoryDto);

    List<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long id);

}
