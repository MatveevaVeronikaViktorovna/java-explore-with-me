package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.Hit;
import ru.practicum.stats.HitRepository;
import ru.practicum.statsDto.HitResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    public void create(Hit hit) {
        Hit newHit = hitRepository.save(hit);
        log.info("Добавлен hit: {}", newHit);
    }

    @Override
    public List<HitResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (uris != null) {
            if (unique) {
                return hitRepository.findDistinctByTimestampAndUris(start, end, uris);
            } else {
                return hitRepository.findAllByTimestampAndUris(start, end, uris);
            }
        } else {
            if (unique) {
                return hitRepository.findDistinctByTimestamp(start, end);
            } else {
                return hitRepository.findAllByTimestamp(start, end);
            }
        }
    }

}
