package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.enums.ParticipationRequestStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Integer countAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    List<ParticipationRequest> findAllByRequesterId(Long userId);

    Optional<ParticipationRequest> findAllByIdAndRequesterId(Long requestId, Long userId);

    List<ParticipationRequest> findAllByEventIdAndEventInitiatorId(Long eventId, Long userId);

    List<ParticipationRequest> findAllByEventIdAndEventInitiatorIdAndIdIn(Long eventId, Long userId, List<Long> requestsId);
}
