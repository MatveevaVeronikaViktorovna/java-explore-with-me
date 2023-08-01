package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.CategoryDtoMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryDtoMapper mapper = Mappers.getMapper(CategoryDtoMapper.class);

    @Transactional
    @Override
    public CategoryDto create(CategoryDto categoryDto) {
        Category category = mapper.dtoToCategory(categoryDto);
        Category newCategory = categoryRepository.save(category);
        log.info("Добавлена категория: {}", newCategory);
        return mapper.categoryToDto(newCategory);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            log.warn("Категория с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", id));
        }
        categoryRepository.deleteById(id);
        log.info("Удалена категория с id {}", id);
    }

    @Transactional
    @Override
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Категория с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", id));
        });
        category.setName(categoryDto.getName());
        Category updatedCategory = categoryRepository.save(category);
        log.info("Обновлена категория c id {} на {}", id, updatedCategory);
        return mapper.categoryToDto(updatedCategory);
    }
    
}
