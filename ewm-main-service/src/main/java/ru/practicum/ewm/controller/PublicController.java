package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") Integer from,
                                              @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос на получение всех категорий. Параметры: from={}, size={}", from, size);
        return categoryService.getAll(from, size);
    }

    @GetMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("Поступил запрос на получение категории с id={}", catId);
        return categoryService.getById(catId);
    }

}
