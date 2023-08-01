package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CategoryDto;

public interface CategoryService {
    CategoryDto create(CategoryDto categoryDto);

    void delete(Long id);

    CategoryDto update(Long id, CategoryDto categoryDto);

}
