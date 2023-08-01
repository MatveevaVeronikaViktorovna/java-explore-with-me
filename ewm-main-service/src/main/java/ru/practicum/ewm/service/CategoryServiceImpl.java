package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        return null;
    }
}
