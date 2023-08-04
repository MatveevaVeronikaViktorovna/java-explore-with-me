package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.Event.EventFullDto;
import ru.practicum.ewm.dto.Event.NewEventDto;
import ru.practicum.ewm.dto.Event.UpdateEventAdminRequestDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Slf4j
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Поступил запрос на создание пользователя {}", userDto);
        return userService.create(userDto);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Поступил запрос на удаление пользователя с id={}", userId);
        userService.delete(userId);
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос на получение всех пользователей. Параметры: ids={}, from={}, size={}",
                ids, from, size);
        return userService.getAll(ids, from, size);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Поступил запрос на создание категории {}", categoryDto);
        return categoryService.create(categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Поступил запрос на удаление категории с id={}", catId);
        categoryService.delete(catId);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Поступил запрос на обновление категории с id={} на {}", catId, categoryDto);
        return categoryService.update(catId, categoryDto);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequestDto eventDto) {
        log.info("Поступил запрос от администратора на обновление события с id={} на {}", eventId, eventDto);
        return eventService.updateByAdmin(eventId, eventDto);
    }

}
