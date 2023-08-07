package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.EventShortDto;
import ru.practicum.ewm.dto.event.NewEventDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

@Mapper(componentModel = "spring")
public interface EventDtoMapper {

    @Mapping(target = "category", ignore = true)
    Event dtoToEvent(NewEventDto newEventDto);

    EventFullDto eventToDto(Event event);
    EventShortDto eventToShortDto(Event event);

    default Long mapCategoryToCategoryId(Category category) {
        return category.getId();
    }

}
