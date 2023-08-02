package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.LocationDto;
import ru.practicum.ewm.model.Location;

@Mapper
public interface LocationDtoMapper {

    Location dtoToLocation(LocationDto locationDto);

    LocationDto locationToDto(Location location);
}
