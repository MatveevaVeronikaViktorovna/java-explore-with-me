package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.dto.participationRequest.RequestDto;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.User;

@Mapper(componentModel = "spring")
public interface ParticipationRequestDtoMapper {

    RequestDto participationRequestToDto(ParticipationRequest request);

    default Long mapEventToLong(Event event) {
        return event.getId();
    }

    default Long mapUserToLong(User user) {
        return user.getId();
    }

}
