package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicController {

    private final CategoryService categoryService;
    private final EventService eventService;

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

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsByUser(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                                  @RequestParam(required = false) Boolean onlyAvailable,
                                                  @RequestParam(required = false) EventSort sort,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос на получение всех событий. Параметры: text={}, categories={}, paid={}, " +
                        "rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}, from={}, size={}", text, categories,
                paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        return eventService.getAllByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

}
