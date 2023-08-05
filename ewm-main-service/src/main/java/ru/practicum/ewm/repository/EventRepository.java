package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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


         //   "where (cast(us.registration_date as date)) between ?1 and ?2 "+
         //   "group by ev.user_id"

    @Query("SELECT e FROM Event e " +
            "WHERE e.state IN :states ")
    List<Event> findAllByAdmin(@Param("states") List<EventState> states);

}
