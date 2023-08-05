package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);
    List<Event> findAllByCategoryId(Long catId);

    @Query(value = "SELECT event.id, event.annotation, event.category_id, event.created_on, event.description, " +
            "event.event_date, event.initiator_id, event.location_id, event.paid, event.participant_limit, " +
            "event.published_on, event.request_moderation, event.state, event.title " +
            "FROM events", nativeQuery = true)
         //   "where (cast(us.registration_date as date)) between ?1 and ?2 "+
         //   "group by ev.user_id"

    List<Event> findAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd);

}
