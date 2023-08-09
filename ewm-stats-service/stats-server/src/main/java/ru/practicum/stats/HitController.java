package ru.practicum.stats;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.service.HitService;
import ru.practicum.statsDto.HitRequestDto;
import ru.practicum.statsDto.HitResponseDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.statsDto.ConstantsForDto.DATE_TIME_FORMAT;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class HitController {

    private final HitService hitService;
    private final HitDtoMapper mapper = Mappers.getMapper(HitDtoMapper.class);


    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody HitRequestDto hit) {
        log.info("Поступил запрос на cохранение в статистику информации {}", hit);
        hitService.create(mapper.dtoToHit(hit));
    }

    @GetMapping(path = "/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<HitResponseDto> getStats(@RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                         @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("Поступил запрос на получение статистики за период с {} по {} для списка uri {} unique={}",
                start, end, uris, unique);
        return hitService.getStats(start, end, uris, unique);
    }

}
