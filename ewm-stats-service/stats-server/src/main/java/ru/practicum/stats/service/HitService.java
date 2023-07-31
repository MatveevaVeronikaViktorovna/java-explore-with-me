package ru.practicum.stats.service;

import ru.practicum.stats.Hit;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {

    void create(Hit hit);

    List<HitResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
