package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.EventDto;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;

@Mapper(componentModel = "spring")
public interface EventDtoMapper {

    @Mapping(target = "category", ignore = true)
    Event dtoToEvent(EventDto eventDto);

    EventDto eventToDto(Event event);

    default Long mapCategoryToCategoryId(Category category) {
        return category.getId();
    }

}
