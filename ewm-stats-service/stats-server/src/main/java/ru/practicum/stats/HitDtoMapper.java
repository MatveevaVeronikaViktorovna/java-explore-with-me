package ru.practicum.stats;

import org.mapstruct.Mapper;
import ru.practicum.statsDto.HitRequestDto;

@Mapper
public interface HitDtoMapper {
    Hit dtoToHit(HitRequestDto dto);
}
