package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.Create;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.Update;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequestDto;
import ru.practicum.ewm.dto.user.UserDto;
import ru.practicum.ewm.model.enums.EventState;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin")
@Slf4j
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final CompilationService compilationService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("Поступил запрос на создание категории {}", categoryDto);
        return categoryService.create(categoryDto);
    }

    @PatchMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Поступил запрос на обновление категории с id={} на {}", catId, categoryDto);
        return categoryService.update(catId, categoryDto);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("Поступил запрос на удаление категории с id={}", catId);
        categoryService.delete(catId);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(@Validated(Create.class) @RequestBody NewCompilationDto compilationDto) {
        log.info("Поступил запрос на создание подборки событий {}", compilationDto);
        return compilationService.create(compilationDto);
    }

    @PatchMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Validated(Update.class) @RequestBody NewCompilationDto compilationDto) {
        log.info("Поступил запрос на обновление подборки событий с id={} на {}", compId, compilationDto);
        return compilationService.update(compId, compilationDto);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Поступил запрос на удаление подборки событий с id={}", compId);
        compilationService.delete(compId);
    }

    @GetMapping("/events")
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                  @RequestParam(required = false) List<EventState> states,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "0") Integer from,
                                                  @RequestParam(defaultValue = "10") Integer size) {
        log.info("Поступил запрос от администратора на получение всех событий. Параметры: users={}, states={}, " +
                        "categories={}, rangeStart={}, rangeEnd={}, from={}, size={}", users, states, categories,
                rangeStart, rangeEnd, from, size);
        return eventService.getAllByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId,
                                           @Valid @RequestBody UpdateEventAdminRequestDto eventDto) {
        log.info("Поступил запрос от администратора на обновление события с id={} на {}", eventId, eventDto);
        return eventService.updateByAdmin(eventId, eventDto);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Поступил запрос на создание пользователя {}", userDto);
        return userService.create(userDto);
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

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long userId) {
        log.info("Поступил запрос на удаление пользователя с id={}", userId);
        userService.delete(userId);
    }

}
