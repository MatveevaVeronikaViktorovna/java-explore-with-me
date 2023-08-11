package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.exception.ConditionsNotMetException;
import ru.practicum.ewm.exception.EntityNotFoundException;
import ru.practicum.ewm.mapper.CategoryDtoMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.pagination.CustomPageRequest;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryDtoMapper mapper = Mappers.getMapper(CategoryDtoMapper.class);

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = mapper.dtoToCategory(categoryDto);
        Category newCategory = categoryRepository.save(category);
        log.info("Добавлена категория: {}", newCategory);
        return mapper.categoryToDto(newCategory);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable page = CustomPageRequest.of(from, size);
        List<Category> categories = categoryRepository.findAll(page).getContent();
        return categories
                .stream()
                .map(mapper::categoryToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Категория с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", id));
        });
        return mapper.categoryToDto(category);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            log.warn("Категория с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", id));
        });
        category.setName(categoryDto.getName());
        log.info("Обновлена категория c id {} на {}", id, category);
        return mapper.categoryToDto(category);
    }

    @Transactional
    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            log.warn("Категория с id {} не найдена", id);
            throw new EntityNotFoundException(String.format("Category with id=%d was not found", id));
        }

        List<Event> categoryEvents = eventRepository.findAllByCategoryId(id);
        if (!categoryEvents.isEmpty()) {
            log.warn("Существуют события, связанные с категорией");
            throw new ConditionsNotMetException("The category is not empty");
        }

        categoryRepository.deleteById(id);
        log.info("Удалена категория с id {}", id);
    }

}
